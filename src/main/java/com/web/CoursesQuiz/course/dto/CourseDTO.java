package com.web.CoursesQuiz.course.dto;

import java.util.ArrayList;

import com.web.CoursesQuiz.chapter.entity.Chapter;
import com.web.CoursesQuiz.lesson.entity.Lesson;
import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.user.entity.AuditableBase;

import io.swagger.v3.oas.annotations.media.Schema;
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

    private String image;

    private Integer timer;

    @Schema(hidden = true)
    ArrayList<LessonPref> lessonsPref;
    @Schema(hidden = true)
    ArrayList<Question> FinalQuiz;
    @Schema(hidden = true)
    ArrayList<Chapter> chapters;
}
