package com.web.CoursesQuiz.course.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import com.web.CoursesQuiz.course.entity.SolvedCourse;

import jakarta.validation.constraints.NotNull;

@Repository
public interface SolvedCourseRepository extends MongoRepository<SolvedCourse, String> {

    SolvedCourse findByUserId(String userId);

    SolvedCourse findByUserIdAndCourseId(String userId, String courseId);

    @Transactional
    long deleteByUserIdAndCourseId(String userId, String courseId); // Returns the count of deleted documents

    // ArrayList of SolvedCourse by userId
    List<SolvedCourse> findAllByUserId(String userId);

    ArrayList<SolvedCourse> findByCourseId(@NotNull String courseId);
}
