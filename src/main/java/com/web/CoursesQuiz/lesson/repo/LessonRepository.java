package com.web.CoursesQuiz.lesson.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.lesson.entity.Lesson;
import org.springframework.data.domain.Pageable;
import java.util.*;


@Repository
public interface LessonRepository extends MongoRepository <Lesson, String>{

    Lesson findByName(String name);
    Page<Lesson> findAll(Pageable pageable);

}
