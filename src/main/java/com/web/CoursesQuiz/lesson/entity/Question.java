package com.web.CoursesQuiz.lesson.entity;

import java.util.*;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "questions")
public class Question {

    @Id
    private String id;

    private String lessonId;    
    private String courseId;
    private Integer finalQuizIdx;
    private String level;
    private String question;
    private String questionImage;
    // private String image;
    private ArrayList<String> options;
    private String correctAnswer;
    private String explanation;
    private String explanationImage;
    private Boolean free = false;
    private Boolean calc = false;
}
