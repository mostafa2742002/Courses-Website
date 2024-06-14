package com.web.CoursesQuiz.course;

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
        if (course.getFinalQuiz() != null)
            dto.setFinalQuiz(course.getFinalQuiz()); // Same here
        // Not copying audit fields to DTO
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
        if (courseDTO.getFinalQuiz() != null)
            course.setFinalQuiz(courseDTO.getFinalQuiz()); // Same here
        // No audit fields initialized here
        return course;
    }
}
