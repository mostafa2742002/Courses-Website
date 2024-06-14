package com.web.CoursesQuiz.course;

import java.util.ArrayList;

import com.web.CoursesQuiz.lesson.Lesson;
import com.web.CoursesQuiz.lesson.Question;
import com.web.CoursesQuiz.user.AuditableBase;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
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
