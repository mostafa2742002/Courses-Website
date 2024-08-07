package com.web.CoursesQuiz.lesson.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.web.CoursesQuiz.user.entity.AuditableBase;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "SolvedLessons")
public class SolvedLesson extends AuditableBase {

    @Id
    private String id;
    private String level;
    private String lessonId;
    private String userId;
    private ArrayList<Answer> LessonQuestions = new ArrayList<>();
    private Integer grade = 0;
    private Boolean firstTime = true;

}
