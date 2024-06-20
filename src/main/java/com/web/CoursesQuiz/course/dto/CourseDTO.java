package com.web.CoursesQuiz.course.dto;

import java.util.ArrayList;

import com.web.CoursesQuiz.lesson.entity.Lesson;
import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.user.entity.AuditableBase;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO extends AuditableBase{

    @NotNull
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String description;

    ArrayList<Lesson> lessons;
    ArrayList<Question> FinalQuiz;
}
