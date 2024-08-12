package com.web.CoursesQuiz.user.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.user.entity.DiscountValue;

@Repository
public interface DiscountRepository extends MongoRepository<DiscountValue, String> {

}
