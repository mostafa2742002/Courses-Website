package com.web.CoursesQuiz.payments.service;

import java.math.BigDecimal;
import com.braintreegateway.*;
import com.braintreegateway.Transaction.Status;
import com.web.CoursesQuiz.CoursesApplication;
import com.web.CoursesQuiz.user.entity.User;
import com.web.CoursesQuiz.user.repo.UserRepository;
import com.web.CoursesQuiz.user.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BraintreeService {

    private final BraintreeGateway gateway = CoursesApplication.gateway;
    private UserService userService;
    private UserRepository userRepository;

    // private final Status[] TRANSACTION_SUCCESS_STATUSES = new Status[] {
    // Transaction.Status.AUTHORIZED,
    // Transaction.Status.AUTHORIZING,
    // Transaction.Status.SETTLED,
    // Transaction.Status.SETTLEMENT_CONFIRMED,
    // Transaction.Status.SETTLEMENT_PENDING,
    // Transaction.Status.SETTLING,
    // Transaction.Status.SUBMITTED_FOR_SETTLEMENT
    // };

    public String generateClientToken() {
        String clientToken = gateway.clientToken().generate();
        return clientToken;
    }

    public ResponseEntity<String> createTransaction(String nonce, String amount, String userId, String courseId,
            String discountCode, Double discountWallet) {
        BigDecimal decimalAmount;
        try {
            decimalAmount = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }

        User user = userRepository.findById(userId).get();
        if (user == null)
            throw new IllegalArgumentException("User not found");

        if (user.getWallet() < decimalAmount.doubleValue())
            throw new IllegalArgumentException("Insufficient balance in wallet");

        if (discountWallet != null && discountWallet > 0) {
            if (user.getWallet() < discountWallet)
                throw new IllegalArgumentException("Insufficient balance in wallet");
            decimalAmount = decimalAmount.subtract(new BigDecimal(discountWallet));
            user.setWallet(user.getWallet() - discountWallet);
            userRepository.save(user);
        }

        System.out.println("Amount: " + decimalAmount);
        System.out.println("Nonce: " + nonce);
        System.out.println("Gateway: " + gateway);

        TransactionRequest request = new TransactionRequest()
                .amount(decimalAmount)
                .paymentMethodNonce(nonce)
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess() || result.getTransaction() != null) {
            userService.enrollCourse(userId, courseId);
            if (discountCode != null && !discountCode.isEmpty()) {
                userService.useReferralCode(discountCode);
            }
            return ResponseEntity.ok("Payment successful");
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
                errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            return ResponseEntity.badRequest().body("Payment failed: " + errorString);
        }
    }
}
