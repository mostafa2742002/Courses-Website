package com.web.CoursesQuiz.lesson;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.web.CoursesQuiz.course.Course;
import com.web.CoursesQuiz.course.CourseDTO;
import com.web.CoursesQuiz.course.CourseMapper;

@Mapper
public interface LessonMapper {

    LessonMapper INSTANCE = Mappers.getMapper(LessonMapper.class);

    LessonDTO toLessonDto(Lesson lesson);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Lesson toLesson(LessonDTO lessonDTO);
}
