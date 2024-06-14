package com.web.CoursesQuiz.lesson;

import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Question {

    private String question;
    private String image;
    private ArrayList<String> options;
    private String correctAnswer;
    private String explanation;
    private String userAnswer;
}
