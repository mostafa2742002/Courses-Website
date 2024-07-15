package com.web.CoursesQuiz.lesson.entity;

import lombok.Data;

@Data
public class Answer {
    private String questionId = "";
    private String question = "";
    private String correctAnswer = "";
    private String userAnswer = "";
    private String explaination = "";
    private Boolean isCorrect = false;
}