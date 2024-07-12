package com.web.CoursesQuiz.lesson.entity;

import lombok.Data;

@Data
public class Answer {
    private String questionId;
    private String answer = "first time";
    private String isCorrect = "false";
}