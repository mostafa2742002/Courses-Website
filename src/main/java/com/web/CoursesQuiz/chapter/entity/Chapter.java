package com.web.CoursesQuiz.chapter.entity;

import java.util.ArrayList;

import org.springframework.data.mongodb.core.mapping.Document;

import com.web.CoursesQuiz.course.dto.LessonPref;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chapters")
public class Chapter {

    @Id
    private String id;

    private String name;

    private String description;

    @Schema(hidden = true)
    private String courseName;
    private String courseId;

    private ArrayList<LessonPref> lessonsPref = new ArrayList<>();
}
