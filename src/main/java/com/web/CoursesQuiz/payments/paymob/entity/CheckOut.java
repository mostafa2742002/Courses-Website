package com.web.CoursesQuiz.payments.paymob.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOut {

    private String urlToBuy;
    private String originalPrice;
    private String discountedFromWallet;
    private String discountedFromPromoCode;
    private String discountedFromReferralCode;
    private String Total;
}
