package com.web.CoursesQuiz.lesson.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.lesson.entity.Lesson;

import java.util.*;


@Repository
public interface LessonRepository extends MongoRepository <Lesson, String>{

    Optional<Lesson> findByName(String name);
}
