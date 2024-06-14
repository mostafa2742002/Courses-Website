package com.web.CoursesQuiz.lesson;

import nonapi.io.github.classgraph.json.Id;

import java.util.ArrayList;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LessonDTO {

    @NotNull
    private String Id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String courseId;

    private ArrayList<Question> LessonQuestions;
}
