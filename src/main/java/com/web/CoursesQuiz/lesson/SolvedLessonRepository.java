package com.web.CoursesQuiz.lesson;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SolvedLessonRepository extends MongoRepository<SolvedLesson, String> {

    SolvedLesson findByUserId(String userId);

    Optional<SolvedLesson> findByUserIdAndLessonId(String userId, String lessonId);

    @Transactional
    long deleteByUserIdAndLessonId(String userId, String lessonId); // Returns the count of deleted documents
}
