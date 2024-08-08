package com.web.CoursesQuiz.lesson.service;

import java.util.ArrayList;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.CoursesQuiz.chapter.entity.Chapter;
import com.web.CoursesQuiz.chapter.repo.ChapterRepository;
import com.web.CoursesQuiz.course.dto.LessonPref;
import com.web.CoursesQuiz.course.entity.Course;
import com.web.CoursesQuiz.course.entity.PageResponse;
import com.web.CoursesQuiz.course.repo.CourseRepository;
import com.web.CoursesQuiz.exception.ResourceNotFoundException;
import com.web.CoursesQuiz.lesson.dto.LessonDTO;
import com.web.CoursesQuiz.lesson.dto.LessonMapper;
import com.web.CoursesQuiz.lesson.entity.Lesson;
import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.lesson.repo.LessonRepository;
import com.web.CoursesQuiz.lesson.repo.QuestionRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LessonService {

    private LessonRepository lessonRepository;
    private CourseRepository courseRepository;
    private QuestionRepository questionRepository;
    private ChapterRepository chapterRepository;

    public void addLesson(@NotNull LessonDTO lessonDTO, @NotNull String courseId, @NotNull String chapterId) {
        lessonDTO.setId(null);
        if (courseRepository.findById(courseId).isEmpty()) {
            throw new ResourceNotFoundException("Course", "Course Id", courseId);
        }

        if (!chapterRepository.findById(chapterId).isPresent()) {
            throw new ResourceNotFoundException("Chapter", "Chapter Id", chapterId);
        }

        Lesson lesson = LessonMapper.toLesson(lessonDTO);
        lesson.setCourseId(courseId);
        lesson.setChapterId(chapterId);
        Lesson lessonAdded = lessonRepository.save(lesson);

        Course course = courseRepository.findById(courseId).get();

        LessonPref lessonPref = new LessonPref(lessonAdded.getId(), lessonAdded.getName());
        course.getLessonsPref().add(lessonPref);

        Chapter chapter = chapterRepository.findById(chapterId).get();
        chapter.getLessonsPref().add(lessonPref);

        courseRepository.save(course);
        chapterRepository.save(chapter);
    }

    public LessonDTO getLesson(@NotNull String lessonId, @NotNull String level) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson", "Lesson Id", lessonId));
        if (!(level.equals("easy") || level.equals("medium") || level.equals("hard")))
            throw new IllegalArgumentException("Invalid level value should be easy or medium or hard");

        LessonDTO lessonDTO = LessonMapper.toLessonDto(lesson);
        ArrayList<Question> questions = getAllQuestions(lessonId, level);
        lessonDTO.setLessonQuestions(questions);
        return lessonDTO;
    }

    public boolean updateLesson(@Valid LessonDTO lessonDTO) {
        boolean isUpdated = false;
        Lesson lesson = LessonMapper.toLesson(lessonDTO);
        Optional<Lesson> lessonOptional = lessonRepository.findById(lesson.getId());
        if (!lessonOptional.isPresent())
            throw new ResourceNotFoundException(" lesson", " lesson Id", lessonDTO.getId());

        lessonOptional.get().setName(lesson.getName());
        lessonOptional.get().setDescription(lesson.getDescription());
        lessonOptional.get().setCourseId(lesson.getCourseId());
        lessonRepository.save(lessonOptional.get());

        isUpdated = true;

        return isUpdated;
    }

    @Transactional
    public boolean deleteLesson(@NotNull String lessonId) {
        // Ensure lessonId is not null before proceeding
        if (lessonId == null) {
            throw new IllegalArgumentException("Lesson ID must not be null");
        }
        System.out.println("Lesson ID: " + lessonId);
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson", "Lesson Id", lessonId));

        String courseId = lesson.getCourseId();
        if (courseId == null) {
            throw new IllegalStateException("Lesson's Course ID must not be null");
        }

        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        String chapterId = lesson.getChapterId();
        if (chapterId == null) {
            throw new IllegalStateException("Lesson's Chapter ID must not be null");
        }

        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow(
                () -> new ResourceNotFoundException("Chapter", "Chapter Id", chapterId));

        course.getLessonsPref().removeIf(lessonPref -> lessonPref.getId().equals(lessonId));
        courseRepository.save(course);

        chapter.getLessonsPref().removeIf(lessonPref -> lessonPref.getId().equals(lessonId));
        chapterRepository.save(chapter);

        lessonRepository.delete(lesson);
        return true;
    }

    public PageResponse<Lesson> findAllLessons(int page, int size) {
        Page<Lesson> lessonPage = lessonRepository.findAll(PageRequest.of(page, size));

        PageResponse<Lesson> response = new PageResponse<>();
        response.setContent(lessonPage.getContent());
        response.setNumber(lessonPage.getNumber());
        response.setSize(lessonPage.getSize());
        response.setTotalElements(lessonPage.getTotalElements());
        response.setTotalPages(lessonPage.getTotalPages());
        response.setFirst(lessonPage.isFirst());
        response.setLast(lessonPage.isLast());

        return response;
    }

    public void addQuestion(@NotNull Question question, @NotNull String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("lesson", "lesson Id", lessonId));

        if (question.getId() != null)
            question.setId(null);
        if (question.getLessonId() == null)
            throw new ResourceNotFoundException("Lesson Id", "Lesson Id", lessonId);
        if (question.getCourseId() == null)
            throw new ResourceNotFoundException("Course Id", "Course Id", lesson.getCourseId());

        Question question2 = questionRepository.save(question);

        lesson.getLessonQuestionsIds().add(question2.getId());
        lessonRepository.save(lesson);
    }

    public boolean updateQuestion(@NotNull Question question) {
        boolean isUpdated = false;
        if (question.getId() == null)
            throw new ResourceNotFoundException("Question Id", "Question Id", null);
        if (question.getLessonId() == null)
            throw new ResourceNotFoundException("Lesson Id", "Lesson Id", null);
        if (question.getCourseId() == null)
            throw new ResourceNotFoundException("Course Id", "Course Id", null);
        if (questionRepository.findById(question.getId()).isEmpty())
            throw new ResourceNotFoundException("Question", "Question Id", question.getId());

        questionRepository.save(question);

        isUpdated = true;

        return isUpdated;
    }

    public boolean deleteQuestion(@NotNull String questionID) {
        boolean isDeleted = false;
        Question question = questionRepository.findById(questionID).orElseThrow(
                () -> new ResourceNotFoundException("Question", "Question Id", questionID));

        Lesson lesson = lessonRepository.findById(question.getLessonId()).get();
        lesson.getLessonQuestionsIds().remove(questionID);
        lessonRepository.save(lesson);

        questionRepository.delete(question);

        isDeleted = true;

        return isDeleted;
    }

    public ArrayList<Question> getAllQuestions(@NotNull String lessonId, String level) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("lesson", "lesson Id", lessonId));

        ArrayList<Question> questions = new ArrayList<>();
        for (String questionId : lesson.getLessonQuestionsIds()) {
            Question question = questionRepository.findById(questionId).get();
            if (question.getLevel().equals(level))
                questions.add(question);
        }

        return questions;
    }

    public String getLessonName(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson", "Lesson Id", lessonId));
        return lesson.getName();
    }

}
