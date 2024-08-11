package com.web.CoursesQuiz.user.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.user.entity.PromoCode;

@Repository
public interface PromoCodeRepository extends MongoRepository<PromoCode, String> {

    PromoCode findByCode(String code);

}
