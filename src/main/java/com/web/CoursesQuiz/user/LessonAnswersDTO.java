package com.web.CoursesQuiz.user;

import java.util.ArrayList;

import com.web.CoursesQuiz.lesson.Question;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonAnswersDTO {

    private String userId;

    private String lessonId;

    @NotNull
    private ArrayList<Question> QuestionsAnswers = new ArrayList<>();
}
