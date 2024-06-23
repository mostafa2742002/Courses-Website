package com.web.CoursesQuiz.user.entity;

import java.util.ArrayList;

import com.web.CoursesQuiz.course.dto.CourseDTO;
import com.web.CoursesQuiz.course.entity.SolvedCourse;
import com.web.CoursesQuiz.lesson.dto.LessonDTO;
import com.web.CoursesQuiz.lesson.entity.Question;

import lombok.Data;

@Data
public class AttendCourse {

    ArrayList<Question> questions;
    SolvedCourse solvedCourse;
}
