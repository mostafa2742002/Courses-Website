package com.web.CoursesQuiz.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseLessonInfo {

    private String courseId;
    private String courseName;
    private String courseGrade;
    private CourseQuestions courseQuestions;
    private String lessonId;
    private String lessonName;
    private String lessonGrade;
    private LessonQuestions lessonQuestions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseQuestions {
        private String right;
        private String wrong;
        private String notSolved;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LessonQuestions {
        private String right;
        private String wrong;
        private String notSolved;
    }

}
