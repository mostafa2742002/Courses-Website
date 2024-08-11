package com.web.CoursesQuiz.payments.paypal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.CoursesQuiz.dto.ResponseDto;
import com.web.CoursesQuiz.payments.paymob.entity.CheckOut;
import com.web.CoursesQuiz.payments.paypal.service.PaymentPaypalService;

@RestController
@RequestMapping("/payment")
public class PaymentPaypalController {

    @Autowired
    private PaymentPaypalService paymentPaypalService;

    @PostMapping("/create")
    public ResponseEntity<CheckOut> createPaymentIntent(@RequestParam String courseId, @RequestParam String userId,
            @RequestParam String pkgId, @RequestParam(required = false) String referralCode,
            @RequestParam Double discountWallet,
            @RequestParam Boolean IsEgypt,
            @RequestParam(name = "promoCode", defaultValue = "null", required = false) String promoCode) {
        return paymentPaypalService.createPaymentIntent(courseId, userId, pkgId, referralCode, discountWallet, IsEgypt,
                promoCode);
    }
}