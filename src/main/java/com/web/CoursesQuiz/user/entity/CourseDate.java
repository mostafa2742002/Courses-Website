package com.web.CoursesQuiz.user.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDate {

    private String courseId;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private Integer Duration;
    private String courseName;
    private String courseImage;
}
