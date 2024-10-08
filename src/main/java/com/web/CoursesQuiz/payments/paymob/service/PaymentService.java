package com.web.CoursesQuiz.payments.paymob.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.web.CoursesQuiz.course.service.CourseService;
import com.web.CoursesQuiz.dto.ResponseDto;
import com.web.CoursesQuiz.packages.entity.Pkg;
import com.web.CoursesQuiz.packages.repo.PkgRepository;
import com.web.CoursesQuiz.payments.paymob.entity.CheckOut;
import com.web.CoursesQuiz.payments.paymob.entity.callback.Order;
import com.web.CoursesQuiz.payments.paymob.entity.callback.TransactionCallback;
import com.web.CoursesQuiz.payments.paymob.entity.request.BillingData;
import com.web.CoursesQuiz.payments.paymob.entity.request.Item;
import com.web.CoursesQuiz.payments.paymob.entity.request.PaymentIntentRequest;
import com.web.CoursesQuiz.payments.paymob.entity.response.PaymentResponse;
import com.web.CoursesQuiz.payments.paymob.entity.user.UserPayment;
import com.web.CoursesQuiz.payments.paymob.repo.UserPaymentRepository;
import com.web.CoursesQuiz.user.entity.CourseDate;
import com.web.CoursesQuiz.user.entity.DiscountValue;
import com.web.CoursesQuiz.user.entity.PromoCode;
import com.web.CoursesQuiz.user.entity.User;
import com.web.CoursesQuiz.user.repo.DiscountRepository;
import com.web.CoursesQuiz.user.repo.PromoCodeRepository;
import com.web.CoursesQuiz.user.repo.ReferralCodeRepository;
import com.web.CoursesQuiz.user.repo.UserRepository;
import com.web.CoursesQuiz.user.service.UserService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

  private final WebClient webClient;
  private final UserRepository userRepository;
  private final UserPaymentRepository userPaymentRepository;
  private final UserService userService;
  private final CourseService courseService;
  private final PkgRepository pkgRepository;
  private final PromoCodeRepository promoCodeRepository;
  private final ReferralCodeRepository referralCodeRepository;
  private final DiscountRepository discountRepository;

  @Value("${paymob.api.secretkey}")
  private String secretKey;

  @Value("${card.integration.id}")
  private int CardIntegrationId;

  @Value("${wallet.integration.id}")
  private int WalletIntegrationId;

  @Value("${paymob.api.publicKey}")
  private String publicKey;

  public PaymentService(WebClient.Builder webClientBuilder, UserRepository userRepository,
      UserPaymentRepository userPaymentRepository, UserService userService,
      CourseService courseService, PkgRepository pkgRepository, PromoCodeRepository promoCodeRepository,
      ReferralCodeRepository referralCodeRepository, DiscountRepository discountRepository) {
    this.webClient = webClientBuilder.baseUrl("https://accept.paymob.com/v1/intention").build();
    this.userRepository = userRepository;
    this.userPaymentRepository = userPaymentRepository;
    this.userService = userService;
    this.courseService = courseService;
    this.pkgRepository = pkgRepository;
    this.promoCodeRepository = promoCodeRepository;
    this.referralCodeRepository = referralCodeRepository;
    this.discountRepository = discountRepository;
  }

  public ResponseEntity<CheckOut> createPaymentIntent(String courseId, String userId, String pkgId,
      String referralCode, Double discountWallet, Boolean IsEgypt, String promoCode) {

    // validate that the user has enough money in his wallet greater than or equal
    // to the amount

    if (pkgId != null && !pkgId.isEmpty()) {
      if (pkgRepository.findById(pkgId).isEmpty()) {
        throw new RuntimeException("Package not found");
      }
    }

    if(discountWallet == null) {
      discountWallet = 0.0;
    }

    Pkg pkg = pkgRepository.findById(pkgId).get();
    if (pkg == null) {
      throw new RuntimeException("Package not found");
    }

    int amount = 0;
    if (IsEgypt) {
      amount = pkg.getPriceForEgypt();
    } else {
      amount = pkg.getPriceForNonEgypt();
    }
    int expiryDate = pkg.getDurationByMonths();

    User checkUser = userRepository.findById(userId).get();
    if (checkUser == null) {
      throw new RuntimeException("User not found");
    }

    if (checkUser.getReferralCode().equals(referralCode)) {
      throw new RuntimeException("User cannot use his own referral code");
    }

    if (checkUser.getUsedReferralCodes().contains(referralCode)) {
      throw new RuntimeException("User has already used this referral code");
    }

    if (checkUser.getWallet() < discountWallet && discountWallet > 0) {
      throw new RuntimeException("User wallet balance is less than the amount");
    }

    if (!promoCode.equals("null") && checkUser.getUserPromoCodes().contains(promoCode)) {
      throw new RuntimeException("User has already used this promo code");
    }

    UserPayment checkUserPayment = userPaymentRepository.findByUserIdAndCourseId(userId, courseId);
    if (checkUserPayment != null) {
      userPaymentRepository.delete(checkUserPayment);
    }
    // we will deduct the amount from the user wallet when the payment is successful

    amount -= discountWallet;

    if (!promoCode.equals("null")) {
      if (promoCodeRepository.findByCode(promoCode) == null) {
        throw new RuntimeException("Promo code not found");
      }

      // we want to check if it expired or not. we have how it long by months and we
      // have created date
      PromoCode promoCode1 = promoCodeRepository.findByCode(promoCode);
      if (promoCode1.getExpiryDateByMonth() != null) {
        LocalDate expiryDate1 = promoCode1.getCreatedDate();
        expiryDate1 = expiryDate1.plusMonths(promoCode1.getExpiryDateByMonth());
        if (LocalDate.now().isAfter(expiryDate1)) {
          throw new RuntimeException("Promo code is expired");
        }
      }
      amount -= promoCodeRepository.findByCode(promoCode).getDiscount();
    }

    if (!referralCode.equals("null")) {
      if (referralCodeRepository.findByCode(referralCode) == null) {
        throw new RuntimeException("Referral code not found");
      }

      DiscountValue discountValue = discountRepository.findAll().get(0);
      Double DISCOUNT_VALUE = Double.parseDouble(discountValue.getValue());
      amount -= DISCOUNT_VALUE;
    }

    amount = Math.max(amount, 0);

    PaymentIntentRequest request = new PaymentIntentRequest();
    request.setAmount(amount * 100);
    request.setCurrency("EGP");
    request.setPayment_methods(new int[] { CardIntegrationId, WalletIntegrationId });
    request.setItems(new Item[] {
        new Item(courseId, amount * 100, 1)
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

            throw new RuntimeException("Error occurred: " + we.getResponseBodyAsString());
          }
          System.out.println("Error occurred: " + error.getMessage());
          throw new RuntimeException("Error occurred: " + error.getMessage());
        });

    // if there is no error we will add the payment intent to the payments
    // repository
    response.subscribe(paymentResponse -> {
      UserPayment userPayment = new UserPayment();
      userPayment.setUserId(userId);
      userPayment.setUserEmail(user.getEmail());
      userPayment.setCourseId(courseId);
      if (!referralCode.equals("null"))
        userPayment.setReferralCode(referralCode);
      if (!promoCode.equals("null"))
        userPayment.setPromoCode(promoCode);
      LocalDate expiryDate1 = LocalDate.now().plusMonths(expiryDate);
      userPayment.setExpiryDate(expiryDate1);
      LocalDate createdDate = LocalDate.now();
      userPayment.setCreationDate(createdDate);
      userPayment.setDuration(pkg.getDurationByMonths());
      userPayment.setPaymentId(paymentResponse.getId());
      userPaymentRepository.save(userPayment);
    });

    String userURL = "https://accept.paymob.com/unifiedcheckout/?publicKey=" + publicKey + "&clientSecret="
        + response.block().getClient_secret();

    CheckOut checkOut = new CheckOut();
    checkOut.setUrlToBuy(userURL);
    checkOut.setOriginalPrice(String.valueOf(amount));
    checkOut.setDiscountedFromWallet(String.valueOf(discountWallet));
    if (!promoCode.equals("null"))
      checkOut.setDiscountedFromPromoCode(promoCodeRepository.findByCode(promoCode).getDiscount().toString());
    else
      checkOut.setDiscountedFromPromoCode("0");
    if (!referralCode.equals("null")) {
      DiscountValue discount = discountRepository.findAll().get(0);
      Integer DISCOUNT_VALUE = Integer.parseInt(discount.getValue());
      checkOut.setDiscountedFromReferralCode(String.valueOf(DISCOUNT_VALUE));

    } else
      checkOut.setDiscountedFromReferralCode("0");
    checkOut.setTotal(String.valueOf(amount));
    checkOut.setStartDate(LocalDate.now());
    checkOut.setExpiryDate(LocalDate.now().plusMonths(expiryDate));
    checkOut.setDuration(expiryDate);

    return ResponseEntity.status(HttpStatus.OK).body(checkOut);
  }

  public void handleCallback(TransactionCallback transactionCallback) {
    if (!transactionCallback.getObj().isSuccess())
      return;
    // we will print a profissional logs here

    System.out.println("Payment successful");
    Order order = transactionCallback.getObj().getOrder();
    System.out.println("Order ID: " + order.getId());
    UserPayment userPayment = userPaymentRepository.findByUserEmail(order.getShippingData().getEmail());
    System.out.println(userPayment);

    if (userPayment == null) {
      throw new RuntimeException("User payment not found");
    }

    String referralCode = userPayment.getReferralCode();
    Double discountWallet = userPayment.getDiscountWallet();
    String promoCode = userPayment.getPromoCode();

    System.out.println("Referral code: " + referralCode);
    System.out.println("Discount wallet: " + discountWallet);
    System.out.println("Promo code: " + promoCode);

    if (referralCode != null && !referralCode.isEmpty()) {
      userService.useReferralCode(referralCode);
    }

    User user = userRepository.findById(userPayment.getUserId()).get();
    System.out.println(user);
    if (user == null) {
      throw new RuntimeException("User not found");
    }

    // Deduct from wallet if applicable
    if (discountWallet != null && discountWallet > 0) {
      user.setWallet(user.getWallet() - discountWallet);
      userRepository.save(user);
    }

    user.getUsedReferralCodes().add(referralCode);
    user.getUserPromoCodes().add(promoCode);

    CourseDate courseDate = new CourseDate();
    courseDate.setCourseId(userPayment.getCourseId());
    courseDate.setExpiryDate(userPayment.getExpiryDate());
    courseDate.setDuration(userPayment.getDuration());
    courseDate.setStartDate(LocalDate.now());
    courseDate.setCourseName(courseService.getCourseName(userPayment.getCourseId()));
    courseDate.setCourseImage(courseService.getCourseImage(userPayment.getCourseId()));
    user.getCourses().add(courseDate);
    System.out.println(courseDate);
    userRepository.save(user);

    userPaymentRepository.delete(userPayment);
  }

}
