package com.web.CoursesQuiz.lesson.entity;

import lombok.Data;

@Data
public class Answer {
    private String questionId;
    private String answer = "";
    private String isCorrect = "false";
}