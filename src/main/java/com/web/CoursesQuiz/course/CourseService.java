package com.web.CoursesQuiz.course;

import java.util.List;

import org.springframework.stereotype.Service;

import java.util.*;
import com.web.CoursesQuiz.exception.ResourceNotFoundException;
import com.web.CoursesQuiz.lesson.Question;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CourseService {

    private CourseRepository courseRepository;

    public void addCourse(CourseDTO courseDTO) {
        Course course = CourseMapper.INSTANCE.toCourse(courseDTO);

        Optional<Course> courseOptional = courseRepository.findByName(course.getName());
        if (courseOptional.isPresent()) {
            throw new IllegalStateException("Course already exists");
        }

        courseRepository.save(course);
    }

    public CourseDTO getCourse(@NotNull String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        return CourseMapper.INSTANCE.toCourseDto(course);
    }

    public boolean updateCourse(@Valid CourseDTO courseDTO) {
        boolean isUpdated = false;
        Course course = CourseMapper.INSTANCE.toCourse(courseDTO);
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

        courseRepository.delete(course);
        isDeleted = true;

        return isDeleted;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses;

    }

    public void addQuestion(@NotNull Question question, @NotNull String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        course.getFinalQuiz().add(question);
        courseRepository.save(course);
    }

    public List<Question> getAllQuestions(@NotNull String courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        return course.getFinalQuiz();
    }

    public boolean deleteQuestion(@NotNull String courseId, @NotNull String questionIndex) {
        boolean isDeleted = false;
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        if (course.getFinalQuiz().size() <= Integer.parseInt(questionIndex))
            throw new ResourceNotFoundException("Question", "Question Index", questionIndex);

        course.getFinalQuiz().remove(Integer.parseInt(questionIndex));
        courseRepository.save(course);
        isDeleted = true;

        return isDeleted;
    }

    public boolean updateQuestion(@NotNull Question question, @NotNull String courseId, @NotNull String questionIndex) {
        boolean isUpdated = false;
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Course Id", courseId));

        if (course.getFinalQuiz().size() <= Integer.parseInt(questionIndex))
            throw new ResourceNotFoundException("Question", "Question Index", questionIndex);

        course.getFinalQuiz().set(Integer.parseInt(questionIndex), question);
        courseRepository.save(course);
        isUpdated = true;

        return isUpdated;
    }

}
