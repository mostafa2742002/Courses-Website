package com.web.CoursesQuiz.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseInfo {

    private String courseId;
    private Integer quizidx;
    private String courseName;
    private String courseGrade;
    private CourseQuestions courseQuestions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseQuestions {
        private String right;
        private String wrong;
        private String notSolved;
    }
}
