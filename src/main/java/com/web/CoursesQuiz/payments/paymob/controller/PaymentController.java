package com.web.CoursesQuiz.payments.paymob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.web.CoursesQuiz.dto.ResponseDto;

import com.web.CoursesQuiz.payments.paymob.entity.callback.TransactionCallback;

import com.web.CoursesQuiz.payments.paymob.service.PaymentService;

import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/create-payment-intent")
    public ResponseEntity<ResponseDto> createPaymentIntent(@RequestParam("pkg_id") String pkgId,
            @RequestParam("course_id") String courseId, @RequestParam("user_id") String userId,
            @RequestParam(name = "referralCode", defaultValue = "0", required = false) String referralCode,
             @RequestParam("discount_wallet") Double discountWallet) {
        return paymentService.createPaymentIntent(courseId, userId, pkgId ,referralCode, discountWallet);
    }

    @PostMapping("/callback")
    public void callback(@RequestBody TransactionCallback transactionCallback) {
        
        paymentService.handleCallback(transactionCallback);
    }
}
