package com.web.CoursesQuiz.user.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "promoCodes")
public class PromoCode {

    @Id
    private String id;
    private String code;
    private Integer discount;
    private Integer expiryDateByMonth;
}
