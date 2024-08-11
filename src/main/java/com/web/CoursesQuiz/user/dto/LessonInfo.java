package com.web.CoursesQuiz.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonInfo {

    private String courseId;
    private String chapterId;
    private String chapterName;
    private String level;
    private String courseName;
    private String lessonId;
    private String lessonName;
    private String lessonGrade;
    private LessonQuestions lessonQuestions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LessonQuestions {
        private String right;
        private String wrong;
        private String notSolved;
    }
    
}
