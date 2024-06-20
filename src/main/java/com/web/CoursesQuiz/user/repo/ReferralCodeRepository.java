package com.web.CoursesQuiz.user.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.user.entity.ReferralCode;

@Repository
public interface ReferralCodeRepository extends MongoRepository<ReferralCode, String>{

    ReferralCode findByCode(String code);
}
