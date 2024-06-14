package com.web.CoursesQuiz.lesson;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.web.CoursesQuiz.user.AuditableBase;
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
    private String lessonId;
    private String userId;
    private ArrayList<Question> LessonQuestions = new ArrayList<>();
}
