// package com.web.CoursesQuiz.payments.braintree.controller;

// import com.web.CoursesQuiz.payments.braintree.entity.BuyCourseDTO;
// import com.web.CoursesQuiz.payments.braintree.service.BraintreeService;

// import io.swagger.v3.oas.annotations.parameters.RequestBody;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/braintree")
// public class BraintreeController {

//     private final BraintreeService braintreeService;

//     @Autowired
//     public BraintreeController(BraintreeService braintreeService) {
//         this.braintreeService = braintreeService;
//     }

//     @GetMapping("/token")
//     public String getToken() {
//         return braintreeService.generateClientToken();
//     }

//     @PostMapping("/checkout")
//     public ResponseEntity<String> checkout(@RequestBody BuyCourseDTO buyCourseDTO) {
//         String nonce = buyCourseDTO.getNonce();
//         String amount = buyCourseDTO.getAmount();
//         String userId = buyCourseDTO.getUserId();
//         String courseId = buyCourseDTO.getCourseId();
//         String discountCode = buyCourseDTO.getDiscountCode();
//         Double discountWallet = buyCourseDTO.getDiscountWallet();
//         int expiryDate = buyCourseDTO.getExpiryDate();
//         ResponseEntity<String> result = braintreeService.createTransaction(nonce, amount, userId, courseId, discountCode, discountWallet,expiryDate);
//         return result;
//     }
// }
