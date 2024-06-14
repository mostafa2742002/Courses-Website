package com.web.CoursesQuiz.lesson;

public class LessonMapper {

    public static LessonDTO toLessonDto(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setName(lesson.getName());
        dto.setDescription(lesson.getDescription());
        dto.setCourseId(lesson.getCourseId());
        dto.setLessonQuestions(lesson.getLessonQuestions()); // Assuming this is a direct mapping
        // Not copying audit fields to DTO
        return dto;
    }

    public static Lesson toLesson(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        lesson.setId(lessonDTO.getId());
        lesson.setName(lessonDTO.getName());
        lesson.setDescription(lessonDTO.getDescription());
        lesson.setCourseId(lessonDTO.getCourseId());
        lesson.setLessonQuestions(lessonDTO.getLessonQuestions()); // Assuming this is a direct mapping
        // No audit fields initialized here
        return lesson;
    }
}
