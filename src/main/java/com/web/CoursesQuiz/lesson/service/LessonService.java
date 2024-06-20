package com.web.CoursesQuiz.lesson.service;

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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LessonService {

    private LessonRepository lessonRepository;
    private CourseRepository courseRepository;

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

        return LessonMapper.toLessonDto(lesson);
    }

    public boolean updateLesson(@Valid LessonDTO lessonDTO) {
        boolean isUpdated = false;
        Lesson lesson = LessonMapper.toLesson(lessonDTO);
        Optional<Lesson> lessonOptional = lessonRepository.findById(lesson.getId());
        if (!lessonOptional.isPresent())
            throw new ResourceNotFoundException(" lesson", " lesson Id", lessonDTO.getId());

        lessonRepository.save(lesson);
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

        lesson.getLessonQuestions().add(question);
        lessonRepository.save(lesson);
    }

    public boolean updateQuestion(@NotNull Question question, @NotNull String lessonId,
            @NotNull String questionIndex) {
        boolean isUpdated = false;
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("lesson", "lesson Id", lessonId));

        if (lesson.getLessonQuestions().size() <= Integer.parseInt(questionIndex))
            throw new ResourceNotFoundException("Question", "Question Index", questionIndex);

        lesson.getLessonQuestions().set(Integer.parseInt(questionIndex), question);
        lessonRepository.save(lesson);
        isUpdated = true;

        return isUpdated;
    }

    public boolean deleteQuestion(@NotNull String lessonId, @NotNull String questionIndex) {
        boolean isDeleted = false;
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("lesson", "lesson Id", lessonId));

        if (lesson.getLessonQuestions().size() <= Integer.parseInt(questionIndex))
            throw new ResourceNotFoundException("Question", "Question Index", questionIndex);

        lesson.getLessonQuestions().remove(Integer.parseInt(questionIndex));
        lessonRepository.save(lesson);
        isDeleted = true;

        return isDeleted;
    }

    public List<Question> getAllQuestions(@NotNull String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("lesson", "lesson Id", lessonId));

        return lesson.getLessonQuestions();
    }

}
