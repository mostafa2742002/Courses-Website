package com.web.CoursesQuiz.lesson.entity;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Answer {
    private String questionId = "";
    private String question = "";
    private String questionImage = "";
    private String correctAnswer = "";
    private String image = "";
    private String userAnswer = "";
    private ArrayList<String> options;
    private String explaination = "";
    private String explainationImage = "";
    private Boolean free = false;
    private String level;
    private Boolean calc = false;
    private Boolean isCorrect = false;
}