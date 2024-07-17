package com.web.CoursesQuiz.user.entity;

import java.util.ArrayList;

import com.web.CoursesQuiz.lesson.entity.Answer;

import lombok.Data;

@Data
public class SubmitCourseDTO {

    
    private String userId;
    private String courseId;
    ArrayList<Answer> answers;
}
