package com.web.CoursesQuiz.course.entity;

import java.util.ArrayList;

import org.springframework.data.mongodb.core.mapping.Document;

import com.web.CoursesQuiz.lesson.entity.*;
import com.web.CoursesQuiz.user.entity.AuditableBase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "SolvedCourses")
public class SolvedCourse extends AuditableBase {

    @Id
    private String id;
    private String courseId;
    private String userId;
    private ArrayList<Answer> FinalQuiz = new ArrayList<>();
    private Boolean firstTime = true;
    private Integer grade = 0;
    private Integer quizIdx;
    private Boolean solved = false;

}
