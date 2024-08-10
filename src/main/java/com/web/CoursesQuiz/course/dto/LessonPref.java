package com.web.CoursesQuiz.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonPref {

    private String id;
    private String name;
    private Integer freeQuestions;
    private Integer allQuestions;
}
