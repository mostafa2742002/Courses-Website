package com.web.CoursesQuiz.payments.paymob.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.web.CoursesQuiz.dto.ResponseDto;
import com.web.CoursesQuiz.payments.paymob.entity.callback.Order;
import com.web.CoursesQuiz.payments.paymob.entity.callback.TransactionCallback;
import com.web.CoursesQuiz.payments.paymob.entity.request.BillingData;
import com.web.CoursesQuiz.payments.paymob.entity.request.Item;
import com.web.CoursesQuiz.payments.paymob.entity.request.PaymentIntentRequest;
import com.web.CoursesQuiz.payments.paymob.entity.response.PaymentResponse;
import com.web.CoursesQuiz.payments.paymob.entity.user.UserPayment;
import com.web.CoursesQuiz.payments.paymob.repo.UserPaymentRepository;
import com.web.CoursesQuiz.user.entity.CourseDate;
import com.web.CoursesQuiz.user.entity.User;
import com.web.CoursesQuiz.user.repo.UserRepository;

import reactor.core.publisher.Mono;

@Service
public class PaymentService {

  private final WebClient webClient;
  private final UserRepository userRepository;
  private final UserPaymentRepository userPaymentRepository;

  @Value("${paymob.api.secretkey}")
  private String secretKey;

  @Value("${card.integration.id}")
  private int CardIntegrationId;

  @Value("${wallet.integration.id}")
  private int WalletIntegrationId;

  @Value("${paymob.api.publicKey}")
  private String publicKey;

  public PaymentService(WebClient.Builder webClientBuilder, UserRepository userRepository,
      UserPaymentRepository userPaymentRepository) {
    this.webClient = webClientBuilder.baseUrl("https://accept.paymob.com/v1/intention").build();
    this.userRepository = userRepository;
    this.userPaymentRepository = userPaymentRepository;
  }

  public ResponseEntity<ResponseDto> createPaymentIntent(int amount, String courseId, String userId, int expiryDate) {
    PaymentIntentRequest request = new PaymentIntentRequest();
    request.setAmount(amount);
    request.setCurrency("EGP");
    request.setPayment_methods(new int[] { CardIntegrationId, WalletIntegrationId });
    request.setItems(new Item[] {
        new Item(courseId, amount, 1)
    });

    User user = userRepository.findById(userId).get();
    if (user == null) {
      throw new RuntimeException("User not found");
    }

    request.setBilling_data(new BillingData(user.getFirst_name(), user.getLast_name(), "+2" + user.getPhone(),
        "egypt", user.getEmail()));

    Mono<PaymentResponse> response = webClient.post()
        .uri("/")
        .header("Authorization",
            secretKey)
        .header("Content-Type", "application/json")
        .body(Mono.just(request), PaymentIntentRequest.class)
        .retrieve()
        .bodyToMono(PaymentResponse.class)
        .doOnError(error -> {
          if (error instanceof WebClientResponseException) {
            WebClientResponseException we = (WebClientResponseException) error;
            System.out.println("Response Body: " + we.getResponseBodyAsString());
            System.out.println("Status Text: " + we.getStatusText());
          }
          System.out.println("Error occurred: " + error.getMessage());
        });

    // if there is no error we will add the payment intent to the payments
    // repository
    response.subscribe(paymentResponse -> {
      UserPayment userPayment = new UserPayment();
      userPayment.setUserId(userId);
      userPayment.setCourseId(courseId);
      LocalDate expiryDate1 = LocalDate.now().plusMonths(expiryDate); 
      userPayment.setExpiryDate(expiryDate1);
      userPayment.setPaymentId(paymentResponse.getId());
      userPaymentRepository.save(userPayment);
    });

    String userURL = "https://accept.paymob.com/unifiedcheckout/?publicKey=" + publicKey + "&clientSecret="
        + response.block().getClient_secret();

    return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Payment intent created successfully", userURL));
  }

  public void handleCallback(TransactionCallback transactionCallback) {
    if (!transactionCallback.getObj().isSuccess())
      return;
    Order order = transactionCallback.getObj().getOrder();
    
    UserPayment userPayment = userPaymentRepository.findByUserId(order.getShippingData().getEmail());

    if(userPayment == null) {
      throw new RuntimeException("User payment not found");
    }

    User user = userRepository.findById(userPayment.getUserId()).get();
    if (user == null) {
      throw new RuntimeException("User not found");
    }

    CourseDate courseDate = new CourseDate();
    courseDate.setCourseId(userPayment.getCourseId());
    courseDate.setExpiryDate(userPayment.getExpiryDate());
    user.getCourses().add(courseDate);
    userRepository.save(user);

    userPaymentRepository.delete(userPayment);
  }

}
