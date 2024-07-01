package com.web.CoursesQuiz.payments.paymob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.web.CoursesQuiz.dto.ResponseDto;
import com.web.CoursesQuiz.payments.paymob.entity.callback.Order;
import com.web.CoursesQuiz.payments.paymob.entity.callback.ShippingData;
import com.web.CoursesQuiz.payments.paymob.entity.callback.TransactionCallback;
import com.web.CoursesQuiz.payments.paymob.entity.response.PaymentResponse;
import com.web.CoursesQuiz.payments.paymob.service.PaymentService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/create-payment-intent")
    public ResponseEntity<ResponseDto> createPaymentIntent(@RequestParam("amount") int amount,
            @RequestParam("course_id") String courseId, @RequestParam("user_id") String userId,
            @RequestParam("expiry_date") int expiryDate) {
        return paymentService.createPaymentIntent(amount, courseId, userId, expiryDate);
    }

    @PostMapping("/callback")
    public void callback(@RequestBody TransactionCallback transactionCallback) {
        
        paymentService.handleCallback(transactionCallback);
    }
}
