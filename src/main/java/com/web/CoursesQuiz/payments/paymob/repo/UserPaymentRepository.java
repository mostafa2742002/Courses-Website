package com.web.CoursesQuiz.payments.paymob.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.web.CoursesQuiz.payments.paymob.entity.user.UserPayment;

@Repository
public interface UserPaymentRepository extends MongoRepository<UserPayment, String>{
        
        UserPayment findByUserIdAndCourseId(String userId, String courseId);
        
        UserPayment findByPaymentId(String paymentId);
        
        UserPayment findByUserId(String userId);
        
        UserPayment findByCourseId(String courseId);
        
        void deleteByUserIdAndCourseId(String userId, String courseId);
        
        void deleteByPaymentId(String paymentId);
        
        void deleteByUserId(String userId);
        
        void deleteByCourseId(String courseId);

        UserPayment findByUserEmail(String email);
}
