package com.web.CoursesQuiz.course.entity;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CourseFinalExam {

    private Integer timer;
    private ArrayList<String> FinalQuizIds = new ArrayList<>();
}
