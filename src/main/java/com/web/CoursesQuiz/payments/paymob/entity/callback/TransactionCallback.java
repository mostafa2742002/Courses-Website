package com.web.CoursesQuiz.payments.paymob.entity.callback;

import lombok.Data;

@Data
public class TransactionCallback {

    private String type;
    private TransactionObj obj;
    private String issuerBank;

}
