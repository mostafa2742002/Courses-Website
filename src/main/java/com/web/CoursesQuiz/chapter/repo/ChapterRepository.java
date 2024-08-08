package com.web.CoursesQuiz.chapter.repo;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.chapter.entity.Chapter;

import jakarta.validation.constraints.NotNull;

@Repository
public interface ChapterRepository extends MongoRepository<Chapter, String> {
    Chapter findByCourseId(String courseId);

    // return all chapters by courseId
    ArrayList<Chapter> findAllByCourseId(@NotNull String courseId);

}
