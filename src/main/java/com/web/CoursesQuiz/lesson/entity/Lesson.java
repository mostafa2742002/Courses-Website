package com.web.CoursesQuiz.lesson.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import com.web.CoursesQuiz.user.entity.AuditableBase;

import java.util.ArrayList;
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
@Document(collection = "lessons")
public class Lesson extends AuditableBase {

    @Id
    private String id;

    private String courseId;
    private String chapterId;

    private String name;
    private String description;

    private ArrayList<String> LessonQuestionsIds = new ArrayList<>();
}
