package com.web.CoursesQuiz.payments.paypal.service;

import com.web.CoursesQuiz.dto.ResponseDto;
import com.web.CoursesQuiz.payments.paymob.entity.user.UserPayment;
import com.web.CoursesQuiz.payments.paymob.repo.UserPaymentRepository;
import com.web.CoursesQuiz.payments.paypal.entity.Order;
import com.web.CoursesQuiz.payments.paypal.entity.TransactionCallback;
import com.web.CoursesQuiz.payments.paypal.entity.TransactionCallback.CallbackOrder;
import com.web.CoursesQuiz.user.entity.CourseDate;
import com.web.CoursesQuiz.user.entity.User;
import com.web.CoursesQuiz.user.repo.UserRepository;
import com.web.CoursesQuiz.user.service.UserService;
import com.web.CoursesQuiz.course.service.CourseService;
import com.web.CoursesQuiz.packages.entity.Pkg;
import com.web.CoursesQuiz.packages.repo.PkgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PaymentPaypalService {

    @Autowired
    private PaypalService paypalService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPaymentRepository userPaymentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private PkgRepository pkgRepository;

    public ResponseEntity<ResponseDto> createPaymentIntent(String courseId, String userId, String pkgId,
            String referralCode, Double discountWallet, Boolean IsEgypt) {

        Optional<Pkg> optionalPkg = pkgRepository.findById(pkgId);
        if (optionalPkg.isEmpty()) {
        throw new RuntimeException("Package not found");
        }

        Pkg pkg = optionalPkg.get();

        int amount =  0;
        if(IsEgypt) {
            amount = pkg.getPriceForEgypt();
        } else {
            amount = pkg.getPriceForNonEgypt();
        }
        
        int expiryDate =  pkg.getDurationByMonths();

        User checkUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (checkUser.getReferralCode().equals(referralCode)) {
            throw new RuntimeException("User cannot use his own referral code");
        }

        if (referralCode != null  && checkUser.getUsedReferralCodes().contains(referralCode)) {
            throw new RuntimeException("User has already used this referral code");
        }

        if (checkUser.getWallet() < discountWallet && discountWallet > 0) {
            throw new RuntimeException("User wallet balance is less than the amount");
        }

        Order order = new Order(amount, "USD", "paypal", "sale", "Course payment");

        String approvalUrl;
        String paymentId;
        try {
            Payment payment = paypalService.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
                    order.getIntent(), order.getDescription(), "http://localhost:8080/payment/cancel",
                    "http://localhost:8080/payment/success");

            paymentId = payment.getId();
            approvalUrl = payment.getLinks().stream()
                    .filter(link -> link.getRel().equals("approval_url"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Approval URL not found"))
                    .getHref();
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Error creating PayPal payment", e);
        }

        UserPayment userPayment = new UserPayment();
        userPayment.setUserId(userId);
        userPayment.setCourseId(courseId);
        userPayment.setReferralCode(referralCode);
        userPayment.setExpiryDate(LocalDate.now().plusMonths(expiryDate));
        userPayment.setPaymentId(paymentId);
        userPayment.setApprovalUrl(approvalUrl);
        userPaymentRepository.save(userPayment);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto("Payment intent created successfully", approvalUrl));
    }

    public void handleCallback(TransactionCallback transactionCallback, String paymentId) {
        if (!transactionCallback.getObj().isSuccess())
            return;

        UserPayment userPayment = userPaymentRepository.findByPaymentId(paymentId);
        if (userPayment == null) {
            throw new RuntimeException("User payment not found");
        }

        // Process the payment
        String referralCode = userPayment.getReferralCode();
        Double discountWallet = userPayment.getDiscountWallet();

        if (referralCode != null && !referralCode.isEmpty()) {
            userService.useReferralCode(referralCode);
        }

        User user = userRepository.findById(userPayment.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (discountWallet != null && discountWallet > 0) {
            user.setWallet(user.getWallet() - discountWallet);
            userRepository.save(user);
        }

        if(referralCode != null && !referralCode.isEmpty())
        user.getUsedReferralCodes().add(referralCode);

        CourseDate courseDate = new CourseDate();
        courseDate.setCourseId(userPayment.getCourseId());
        courseDate.setExpiryDate(userPayment.getExpiryDate());
        courseDate.setCourseName(courseService.getCourseName(userPayment.getCourseId()));
        courseDate.setCourseImage(courseService.getCourseImage(userPayment.getCourseId()));
        user.getCourses().add(courseDate);
        userRepository.save(user);

        userPaymentRepository.delete(userPayment);
    }

}