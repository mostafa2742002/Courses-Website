package com.web.CoursesQuiz.user.entity;

import java.util.ArrayList;

import com.web.CoursesQuiz.lesson.dto.LessonDTO;
import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.lesson.entity.SolvedLesson;

import lombok.Data;

@Data
public class AttendLesson {

    ArrayList<Question> questions;
    SolvedLesson solvedLesson;
}
