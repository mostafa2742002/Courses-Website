package com.web.CoursesQuiz.course.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.course.entity.Course;

@Repository
public interface CourseRepository  extends MongoRepository <Course, String>{

    Optional<Course> findByName(String name);
}
