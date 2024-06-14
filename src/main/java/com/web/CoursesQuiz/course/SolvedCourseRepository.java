package com.web.CoursesQuiz.course;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface SolvedCourseRepository extends MongoRepository<SolvedCourse, String> {

    SolvedCourse findByUserId(String userId);

    Optional<SolvedCourse> findByUserIdAndCourseId(String userId, String courseId);

    @Transactional
    long deleteByUserIdAndCourseId(String userId, String courseId); // Returns the count of deleted documents
}
