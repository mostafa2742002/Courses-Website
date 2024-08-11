package com.web.CoursesQuiz.user.entity;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.LastModifiedBy;
import lombok.Data;

@Data
public abstract class AuditableBase {

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private LocalDate createdDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDate lastModifiedDate;

}
