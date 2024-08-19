package com.web.CoursesQuiz.lesson.dto;

import java.util.ArrayList;

import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.user.entity.AuditableBase;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class LessonDTO extends AuditableBase{

    @NotNull
    private String Id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String courseId;

    private String courseName;
    private String chapterId;
    private String chapterName;

    private ArrayList<Question> LessonQuestions;
}
