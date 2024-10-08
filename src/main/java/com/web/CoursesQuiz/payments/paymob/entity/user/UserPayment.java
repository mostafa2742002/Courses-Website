package com.web.CoursesQuiz.payments.paymob.entity.user;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "userPayments")
public class UserPayment {

    @Id
    private String id;

    private String userId;
    private String userEmail;
    private String courseId;
    private String paymentId;
    private LocalDate creationDate;
    private Integer duration;
    private LocalDate expiryDate;
    private String referralCode;
    private String promoCode;
    private Double discountWallet;
    private String approvalUrl;

}
