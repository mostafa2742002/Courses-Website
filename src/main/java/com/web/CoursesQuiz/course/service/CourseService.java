package com.web.CoursesQuiz.course.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import com.web.CoursesQuiz.chapter.entity.Chapter;
import com.web.CoursesQuiz.chapter.repo.ChapterRepository;
import com.web.CoursesQuiz.course.dto.CourseDTO;
import com.web.CoursesQuiz.course.dto.CourseMapper;
import com.web.CoursesQuiz.course.dto.LessonPref;
import com.web.CoursesQuiz.course.entity.Course;
import com.web.CoursesQuiz.course.entity.CourseFinalExam;
import com.web.CoursesQuiz.course.entity.PageResponse;
import com.web.CoursesQuiz.course.entity.SolvedCourse;
import com.web.CoursesQuiz.course.repo.CourseRepository;
import com.web.CoursesQuiz.course.repo.SolvedCourseRepository;
import com.web.CoursesQuiz.exception.ResourceNotFoundException;
import com.web.CoursesQuiz.lesson.dto.LessonDTO;
import com.web.CoursesQuiz.lesson.entity.Lesson;
import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.lesson.entity.SolvedLesson;
import com.web.CoursesQuiz.lesson.repo.LessonRepository;
import com.web.CoursesQuiz.lesson.repo.QuestionRepository;
import com.web.CoursesQuiz.lesson.repo.SolvedLessonRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CourseService {

    private CourseRepository courseRepository;
    private QuestionRepository questionRepository;
    private LessonRepository lessonRepository;
    private ChapterRepository chapterRepository;
    private SolvedCourseRepository solvedCourseRepository;
    private SolvedLessonRepository solvedLessonRepository;

    public void addCourse(CourseDTO courseDTO) {
        courseDTO.setId(null);
        Course course = CourseMapper.toCourse(courseDTO);

        Course courseOptional = courseRepository.findByName(course.getName());
        if (courseOptional != null) {
            throw new IllegalStateException("Course already exists");
        }

        courseRepository.save(course);
    }

    public CourseDTO getCourse(@NotNull String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));
        CourseDTO courseDTO = CourseMapper.toCourseDto(course);
        courseDTO.setFinalExams(course.getFinalQuiz());
        return courseDTO;
    }

    public boolean updateCourse(@Valid CourseDTO courseDTO) {
        boolean isUpdated = false;
        Course course = CourseMapper.toCourse(courseDTO);
        Optional<Course> courseOptional = courseRepository.findById(course.getId());
        if (!courseOptional.isPresent())
            throw new ResourceNotFoundException("Course", "Course Id", courseDTO.getId());

        courseRepository.save(course);
        isUpdated = true;

        return isUpdated;
    }

    public boolean deleteCourse(@NotNull String courseId) {
        boolean isDeleted = false;
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        for (CourseFinalExam courseFinalExam : course.getFinalQuiz()) {
            for (String questionId : courseFinalExam.getFinalQuizIds()) {
                Question question = questionRepository.findById(questionId).orElseThrow(
                        () -> new ResourceNotFoundException("Question", "Question Id", questionId));
                questionRepository.delete(question);
            }
        }

        ArrayList<LessonPref> lessons = course.getLessonsPref();
        for (LessonPref lessonPref : lessons) {
            Lesson lesson = lessonRepository.findById(lessonPref.getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Lesson", "Lesson Id", lessonPref.getId()));

            ArrayList<String> lessonQuestionsIds = lesson.getLessonQuestionsIds();
            for (String questionId : lessonQuestionsIds) {
                Question question = questionRepository.findById(questionId).orElseThrow(
                        () -> new ResourceNotFoundException("Question", "Question Id", questionId));
                questionRepository.delete(question);
            }

            ArrayList<SolvedLesson> solvedLessons = solvedLessonRepository.findByLessonId(lesson.getId());
            for (SolvedLesson solvedLesson : solvedLessons) {
                solvedLessonRepository.delete(solvedLesson);
            }

            lessonRepository.delete(lesson);
        }

        ArrayList<String> chapters = course.getChaptersIds();
        for (String chapterId : chapters) {
            Chapter chapter = chapterRepository.findById(chapterId).orElseThrow(
                    () -> new ResourceNotFoundException("Chapter", "Chapter Id", chapterId));
            chapterRepository.delete(chapter);
        }

        ArrayList<SolvedCourse> solvedCourses = solvedCourseRepository.findByCourseId(courseId);
        for (SolvedCourse solvedCourse : solvedCourses) {
            solvedCourseRepository.delete(solvedCourse);
        }

        courseRepository.delete(course);
        isDeleted = true;
        return isDeleted;
    }

    public PageResponse<Course> findAllCourses(int page, int size) {
        Page<Course> coursePage = courseRepository.findAll(PageRequest.of(page, size));

        PageResponse<Course> response = new PageResponse<>();
        response.setContent(coursePage.getContent());
        response.setNumber(coursePage.getNumber());
        response.setSize(coursePage.getSize());
        response.setTotalElements(coursePage.getTotalElements());
        response.setTotalPages(coursePage.getTotalPages());
        response.setFirst(coursePage.isFirst());
        response.setLast(coursePage.isLast());

        return response;
    }

    public void addQuestion(@NotNull Question question, @NotNull String courseId, @NotNull Integer idx) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        if (question.getId() != null)
            question.setId(null);
        if (question.getCourseId() == null)
            throw new ResourceNotFoundException("Course Id", "Course Id", courseId);

        if (course.getFinalQuiz().size() < idx)
            throw new ResourceNotFoundException("Index", "Index", idx.toString());

        Question savedQuestion = questionRepository.save(question);

        if (idx != -1) {
            CourseFinalExam courseFinalExam = course.getFinalQuiz().get(idx);
            courseFinalExam.getFinalQuizIds().add(savedQuestion.getId());
            course.getFinalQuiz().set(idx, courseFinalExam);
            savedQuestion.setFinalQuizIdx(idx);
        } else if (idx == -1) {
            CourseFinalExam courseFinalExam = new CourseFinalExam();
            courseFinalExam.getFinalQuizIds().add(savedQuestion.getId());
            course.getFinalQuiz().add(courseFinalExam);
            savedQuestion.setFinalQuizIdx(course.getFinalQuiz().size() - 1);
        } else
            throw new ResourceNotFoundException("Index", "Index", idx.toString());

        questionRepository.save(savedQuestion);
        courseRepository.save(course);
    }

    public boolean deleteQuestion(@NotNull String questionId) {
        boolean isDeleted = false;
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new ResourceNotFoundException("Question", "Question Id", questionId));

        Course course = courseRepository.findById(question.getCourseId()).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", question.getCourseId()));

        if (course.getFinalQuiz().size() < question.getFinalQuizIdx())
            throw new ResourceNotFoundException("Index", "Index", question.getFinalQuizIdx().toString());

        CourseFinalExam courseFinalExam = course.getFinalQuiz().get(question.getFinalQuizIdx());
        courseFinalExam.getFinalQuizIds().remove(questionId);
        course.getFinalQuiz().set(question.getFinalQuizIdx(), courseFinalExam);

        courseRepository.save(course);

        questionRepository.delete(question);

        isDeleted = true;

        return isDeleted;
    }

    public boolean updateQuestion(@NotNull Question question) {
        boolean isUpdated = false;
        if (question.getId() == null)
            throw new ResourceNotFoundException("Question Id", "Question Id", null);
        if (question.getCourseId() == null)
            throw new ResourceNotFoundException("Course Id", "Course Id", null);
        if (questionRepository.findById(question.getId()).isEmpty())
            throw new ResourceNotFoundException("Question", "Question Id", question.getId());

        questionRepository.save(question);

        isUpdated = true;

        return isUpdated;
    }

    public ArrayList<Question> getAllQuestions(@NotNull String courseId, Integer idx) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        if (course.getFinalQuiz().size() < idx || idx < 0 || idx == null)
            throw new ResourceNotFoundException("Index", "Index", idx.toString());

        ArrayList<Question> questions = new ArrayList<>();
        for (String questionId : course.getFinalQuiz().get(idx).getFinalQuizIds()) {
            Question question = questionRepository.findById(questionId).orElseThrow(
                    () -> new ResourceNotFoundException("Question", "Question Id", questionId));
            questions.add(question);
        }

        return questions;
    }

    public List<Lesson> getCourseLessons(@NotNull String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        List<Lesson> lessons = new ArrayList<>();
        for (LessonPref lessonPref : course.getLessonsPref()) {
            Lesson lesson = lessonRepository.findById(lessonPref.getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Lesson", "Lesson Id", lessonPref.getId()));
            lessons.add(lesson);
        }

        return lessons;
    }

    public String getCourseName(String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));
        return course.getName();
    }

    public String getCourseImage(@NotNull String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));
        return course.getImage();
    }

    @SuppressWarnings("unlikely-arg-type")
    public boolean deleteFinalQuiz(@NotNull String courseId,@RequestParam @NotNull Integer idx) {
        boolean isDeleted = false;
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        if (course.getFinalQuiz().size() < idx)
            throw new ResourceNotFoundException("Index", "Index", idx.toString());

        CourseFinalExam courseFinalExam = course.getFinalQuiz().get(idx);
        for (String questionId : courseFinalExam.getFinalQuizIds()) {
            Question question = questionRepository.findById(questionId).orElseThrow(
                    () -> new ResourceNotFoundException("Question", "Question Id", questionId));
            questionRepository.delete(question);
        }

        course.getFinalQuiz().remove(idx);
        courseRepository.save(course);

        isDeleted = true;

        return isDeleted;
    }
}
