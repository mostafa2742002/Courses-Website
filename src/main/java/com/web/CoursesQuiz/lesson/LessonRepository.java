package com.web.CoursesQuiz.lesson;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public interface LessonRepository extends MongoRepository <Lesson, String>{

    Optional<Lesson> findByName(String name);
}
