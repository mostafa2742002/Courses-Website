package com.web.CoursesQuiz.payments.paypal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.web.CoursesQuiz.payments.paypal.service.PaymentPaypalService;
import com.web.CoursesQuiz.payments.paypal.service.PaypalService;
import com.web.CoursesQuiz.payments.paypal.entity.Order;
import com.web.CoursesQuiz.payments.paypal.entity.TransactionCallback;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PaypalController {

    @Autowired
    PaypalService service;

    @Autowired
    PaymentPaypalService paymentPaypalService;

    public static final String SUCCESS_URL = "payment/success";
    public static final String CANCEL_URL = "payment/cancel";

    @PostMapping("/pay")
    public String payment(@ModelAttribute("order") Order order) {
        try {
            Payment payment = service.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
                    order.getIntent(), order.getDescription(),
                    "https://courses-website-q0gf.onrender.com/payment/cancel", // "http://localhost:8080/" +
                                                                                // CANCEL_URL,
                    "https://courses-website-q0gf.onrender.com/payment/success"); // "http://localhost:8080/" +
                                                                                  // SUCCESS_URL);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }

        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping(value = CANCEL_URL)
    public String cancelPay() {
        return "cancel";
    }

    @GetMapping(value = SUCCESS_URL)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = service.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                // Create and process the callback
                TransactionCallback callback = new TransactionCallback();
                TransactionCallback.TransactionObject transactionObject = new TransactionCallback.TransactionObject();
                transactionObject.setSuccess(true);

                // Populate the callback with order and shipping data
                TransactionCallback.CallbackOrder order = new TransactionCallback.CallbackOrder();
                TransactionCallback.ShippingData shippingData = new TransactionCallback.ShippingData();
                shippingData.setEmail(payment.getPayer().getPayerInfo().getEmail());
                order.setShippingData(shippingData);
                transactionObject.setOrder(order);

                // Set the transaction object
                callback.setObj(transactionObject);

                // Handle the callback
                paymentPaypalService.handleCallback(callback, paymentId);
                return "success";
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/";
    }

}
