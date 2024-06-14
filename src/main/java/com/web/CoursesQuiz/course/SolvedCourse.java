package com.web.CoursesQuiz.course;

import org.springframework.data.mongodb.core.mapping.Document;

import com.web.CoursesQuiz.lesson.Question;
import com.web.CoursesQuiz.user.AuditableBase;
import java.util.ArrayList;
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
@Builder
@Document(collection = "SolvedCourses")
public class SolvedCourse extends AuditableBase {

    @Id
    private String id;
    private String courseId;
    private String userId;
    private ArrayList<Question> FinalQuiz = new ArrayList<>();
}
