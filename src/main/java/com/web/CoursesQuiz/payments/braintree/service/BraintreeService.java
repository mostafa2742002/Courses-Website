package com.web.CoursesQuiz.payments.braintree.service;



import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;

import com.web.CoursesQuiz.user.entity.User;
import com.web.CoursesQuiz.user.repo.UserRepository;
import com.web.CoursesQuiz.user.service.UserService;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BraintreeService {

    private final BraintreeGateway gateway;
    private final UserService userService;
    private final UserRepository userRepository;



    public String generateClientToken() {
        return gateway.clientToken().generate();
    }

    @Transactional
    public ResponseEntity<String> createTransaction(String nonce, String amount, String userId, String courseId,
                                                    String discountCode, Double discountWallet, int expiryDate) {
        BigDecimal decimalAmount = validateAmount(amount);
        User user = validateUser(userId, decimalAmount, discountWallet);

        // Deduct from wallet if applicable
        if (discountWallet != null && discountWallet > 0) {
            user.setWallet(user.getWallet() - discountWallet);
            userRepository.save(user);
        }

        TransactionRequest request = new TransactionRequest()
                .amount(decimalAmount)
                .paymentMethodNonce(nonce)
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess() || result.getTransaction() != null) {
            LocalDate expiryDateLocal = LocalDate.now().plusMonths(expiryDate);
            userService.enrollCourse(userId, courseId,expiryDateLocal);
            if (discountCode != null && !discountCode.isEmpty()) {
                userService.useReferralCode(discountCode);
            }
            return ResponseEntity.ok("Payment successful");
        } else {
            return ResponseEntity.badRequest().body("Payment failed: " + formatBraintreeErrors(result));
        }
    }

    private BigDecimal validateAmount(String amount) {
        try {
            return new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }
    }

    private User validateUser(String userId, BigDecimal amount, Double discountWallet) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found"));

        double totalDeduction = amount.doubleValue();
        if (discountWallet != null) {
            totalDeduction += discountWallet;
        }

        if (user.getWallet() < totalDeduction) {
            throw new IllegalArgumentException("Insufficient balance in wallet");
        }

        return user;
    }

    private String formatBraintreeErrors(Result<Transaction> result) {
        StringBuilder errorString = new StringBuilder();
        for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
            errorString.append("Error: ").append(error.getCode()).append(": ").append(error.getMessage()).append("\n");
        }
        return errorString.toString();
    }
}
