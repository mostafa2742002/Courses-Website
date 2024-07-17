package com.web.CoursesQuiz.user.entity;

import org.springframework.data.mongodb.core.mapping.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

@Document(collection = "referralCodes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralCode {

    @Id
    private String id;

    private String userId;

    private String code;
}
