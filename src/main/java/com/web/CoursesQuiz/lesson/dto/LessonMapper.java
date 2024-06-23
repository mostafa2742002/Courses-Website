package com.web.CoursesQuiz.lesson.dto;

import com.web.CoursesQuiz.lesson.entity.Lesson;

public class LessonMapper {

    public static LessonDTO toLessonDto(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        if (lesson.getId() != null)
            dto.setId(lesson.getId());
        if (lesson.getName() != null)
            dto.setName(lesson.getName());
        if (lesson.getDescription() != null)
            dto.setDescription(lesson.getDescription());
        if (lesson.getCourseId() != null)
            dto.setCourseId(lesson.getCourseId());
        if (lesson.getCreatedBy() != null)
            dto.setCreatedBy(lesson.getCreatedBy());
        if (lesson.getCreatedDate() != null)
            dto.setCreatedDate(lesson.getCreatedDate());
        if (lesson.getLastModifiedBy() != null)
            dto.setLastModifiedBy(lesson.getLastModifiedBy());
        if (lesson.getLastModifiedDate() != null)
            dto.setLastModifiedDate(lesson.getLastModifiedDate());

        return dto;
    }

    public static Lesson toLesson(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        if (lessonDTO.getId() != null)
            lesson.setId(lessonDTO.getId());
        if (lessonDTO.getName() != null)
            lesson.setName(lessonDTO.getName());
        if (lessonDTO.getDescription() != null)
            lesson.setDescription(lessonDTO.getDescription());
        if (lessonDTO.getCourseId() != null)
            lesson.setCourseId(lessonDTO.getCourseId());
        // No audit fields initialized here
        return lesson;
    }
}
