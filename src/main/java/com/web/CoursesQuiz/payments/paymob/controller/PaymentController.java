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
    public ResponseEntity<ResponseDto> createPaymentIntent(@RequestParam("amount") int amount,
            @RequestParam("course_id") String courseId, @RequestParam("user_id") String userId,
            @RequestParam("expiry_date") int expiryDate,
            @RequestParam("discount_code") String discountCode, @RequestParam("discount_wallet") Double discountWallet) {
        return paymentService.createPaymentIntent(amount, courseId, userId, expiryDate, discountCode, discountWallet);
    }

    @PostMapping("/callback")
    public void callback(@RequestBody TransactionCallback transactionCallback) {
        
        paymentService.handleCallback(transactionCallback);
    }
}
