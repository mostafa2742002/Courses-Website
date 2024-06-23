package com.web.CoursesQuiz.lesson.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.lesson.entity.Question;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String>{

}
