package com.web.CoursesQuiz.course.entity;

import java.util.ArrayList;

import org.springframework.data.mongodb.core.mapping.Document;

import com.web.CoursesQuiz.lesson.entity.Lesson;
import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.user.entity.AuditableBase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "courses")
public class Course extends AuditableBase {

    @Id
    private String id;

    private String name;
    private String description;

    private ArrayList<Lesson> lessons = new ArrayList<>();
    private ArrayList<String> FinalQuizIds = new ArrayList<>();
}