package com.web.CoursesQuiz.payments.entity;

import lombok.Data;

@Data
public class BuyCourseDTO {

    private String nonce;
    private String amount;
    private String userId;
    private String courseId;
    private String discountCode;
    private Double discountWallet;
}
