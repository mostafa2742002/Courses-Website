package com.web.CoursesQuiz.user.dto;

import java.util.ArrayList;

import com.web.CoursesQuiz.lesson.entity.Answer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseAnswersDTO {
    
    private String userId;

    private String courseId;

    @NotNull
    private ArrayList<Answer> QuestionsAnswers = new ArrayList<>();
}
