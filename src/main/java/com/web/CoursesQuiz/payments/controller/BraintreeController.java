package com.web.CoursesQuiz.payments.controller;


import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.web.CoursesQuiz.payments.entity.BuyCourseDTO;
import com.web.CoursesQuiz.payments.service.BraintreeService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/braintree")
public class BraintreeController {

    private final BraintreeService braintreeService;

    @Autowired
    public BraintreeController(BraintreeService braintreeService) {
        this.braintreeService = braintreeService;
    }

    @GetMapping("/token")
    public String getToken() {
        return braintreeService.generateClientToken();
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody BuyCourseDTO buyCourseDTO) {
        String nonce = buyCourseDTO.getNonce();
        String amount = buyCourseDTO.getAmount();
        String userId = buyCourseDTO.getUserId();
        String courseId = buyCourseDTO.getCourseId();
        String discountCode = buyCourseDTO.getDiscountCode();
        Double discountWallet = buyCourseDTO.getDiscountWallet();
        ResponseEntity<String> result = braintreeService.createTransaction(nonce, amount, userId, courseId, discountCode, discountWallet);
        return result;
    }
}
