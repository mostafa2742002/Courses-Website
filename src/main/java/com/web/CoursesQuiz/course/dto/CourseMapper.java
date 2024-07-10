package com.web.CoursesQuiz.course.dto;

import com.web.CoursesQuiz.course.entity.Course;

public class CourseMapper {

    public static CourseDTO toCourseDto(Course course) {
        CourseDTO dto = new CourseDTO();
        if (course.getId() != null)
            dto.setId(course.getId());
        if (course.getName() != null)
            dto.setName(course.getName());
        if (course.getDescription() != null)
            dto.setDescription(course.getDescription());
        if (course.getLessons() != null)
            dto.setLessons(course.getLessons());
        if (course.getImage() != null)
            dto.setImage(course.getImage());
        if (course.getCreatedBy() != null)
            dto.setCreatedBy(course.getCreatedBy());
        if (course.getCreatedDate() != null)
            dto.setCreatedDate(course.getCreatedDate());
        if (course.getLastModifiedBy() != null)
            dto.setLastModifiedBy(course.getLastModifiedBy());
        if (course.getLastModifiedDate() != null)
            dto.setLastModifiedDate(course.getLastModifiedDate());

        return dto;
    }

    public static Course toCourse(CourseDTO courseDTO) {
        Course course = new Course();
        if (courseDTO.getId() != null)
            course.setId(courseDTO.getId());
        if (courseDTO.getName() != null)
            course.setName(courseDTO.getName());
        if (courseDTO.getDescription() != null)
            course.setDescription(courseDTO.getDescription());
        if (courseDTO.getLessons() != null)
            course.setLessons(courseDTO.getLessons()); // Directly assign the list if it's fine as is
        if (courseDTO.getImage() != null)
            course.setImage(courseDTO.getImage());
        return course;
    }
}
