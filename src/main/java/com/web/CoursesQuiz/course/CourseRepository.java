package com.web.CoursesQuiz.course;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository  extends MongoRepository <Course, String>{

    Optional<Course> findByName(String name);
}
