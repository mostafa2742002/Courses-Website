package com.web.CoursesQuiz.lesson.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.web.CoursesQuiz.course.entity.Course;
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

    public void addLesson(@NotNull LessonDTO lessonDTO, @NotNull String courseId) {
        lessonDTO.setId(null);
        if (courseRepository.findById(courseId).isEmpty()) {
            throw new ResourceNotFoundException("Course", "Course Id", courseId);
        }

        Lesson lesson = LessonMapper.toLesson(lessonDTO);
        Optional<Lesson> lessonOptional = lessonRepository.findByName(lesson.getName());
        if (lessonOptional.isPresent()) {
            throw new IllegalStateException("Lesson already exists");
        }

        lessonRepository.save(lesson);

        Course course = courseRepository.findById(courseId).get();
        course.getLessons().add(lesson);
        courseRepository.save(course);

    }

    public LessonDTO getLesson(@NotNull String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson", "Lesson Id", lessonId));
        LessonDTO lessonDTO = LessonMapper.toLessonDto(lesson);
        ArrayList<Question> questions = getAllQuestions(lessonId);
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

    public boolean deleteLesson(@NotNull String lessonId) {
        boolean isDeleted = false;
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson", "Lesson Id", lessonId));

        Course course = courseRepository.findById(lesson.getCourseId()).get();
        lessonRepository.delete(lesson);
        isDeleted = true;

        course.getLessons().remove(lesson);
        courseRepository.save(course);

        return isDeleted;
    }

    public List<Lesson> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        return lessons;
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
        if(question.getId() == null)
            throw new ResourceNotFoundException("Question Id", "Question Id", null);
        if(question.getLessonId() == null)
            throw new ResourceNotFoundException("Lesson Id", "Lesson Id", null);
        if(question.getCourseId() == null)
            throw new ResourceNotFoundException("Course Id", "Course Id", null);
        if(questionRepository.findById(question.getId()).isEmpty())
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

    public ArrayList<Question> getAllQuestions(@NotNull String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("lesson", "lesson Id", lessonId));

        ArrayList<Question> questions = new ArrayList<>();
        for (String questionId : lesson.getLessonQuestionsIds()) {
            Question question = questionRepository.findById(questionId).get();
            questions.add(question);
        }

        return questions;
    }

}
