package com.web.CoursesQuiz.pages.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.pages.entity.Pages;

@Repository
public interface PagesRepository extends MongoRepository<Pages, String> {

}
