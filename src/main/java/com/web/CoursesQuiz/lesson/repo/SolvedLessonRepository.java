package com.web.CoursesQuiz.lesson.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.web.CoursesQuiz.lesson.entity.SolvedLesson;

@Repository
public interface SolvedLessonRepository extends MongoRepository<SolvedLesson, String> {

    SolvedLesson findByUserId(String userId);

    SolvedLesson findByUserIdAndLessonId(String userId, String lessonId);

    @Transactional
    long deleteByUserIdAndLessonId(String userId, String lessonId); // Returns the count of deleted documents

    // ArrayList of SolvedLesson by userId
    List<SolvedLesson> findAllByUserId(String userId);

}
