package com.web.CoursesQuiz.course;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.List;

@Repository
public interface CourseRepository  extends MongoRepository <Course, String>{

    Optional<Course> findByName(String name);
}
