package com.web.CoursesQuiz.chapter.service;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.web.CoursesQuiz.chapter.entity.Chapter;
import com.web.CoursesQuiz.chapter.repo.ChapterRepository;
import com.web.CoursesQuiz.course.dto.LessonPref;
import com.web.CoursesQuiz.course.entity.Course;
import com.web.CoursesQuiz.course.repo.CourseRepository;
import com.web.CoursesQuiz.dto.ResponseDto;
import com.web.CoursesQuiz.exception.ResourceNotFoundException;
import com.web.CoursesQuiz.lesson.service.LessonService;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final LessonService lessonService;

    public ResponseEntity<ResponseDto> addChapter(@NotNull Chapter chapter) {

        if (courseRepository.findById(chapter.getCourseId()).isEmpty()) {
            throw new ResourceNotFoundException("Course", "Course Id", chapter.getCourseId());
        }

        Course course = courseRepository.findById(chapter.getCourseId()).get();
        chapter.setCourseName(course.getName());
        Chapter chapterSaved = chapterRepository.save(chapter);
        course.getChaptersIds().add(chapterSaved.getId());
        courseRepository.save(course);
        return ResponseEntity.ok(new ResponseDto("201", "Chapter added successfully"));
    }

    public Chapter getChapter(@NotNull String chapterId) {
        return chapterRepository.findById(chapterId).orElseThrow(
                () -> new ResourceNotFoundException("Chapter", "Chapter Id", chapterId));
    }

    public boolean updateChapter(Chapter chapter) {
        boolean isUpdated = false;
        Chapter chapterOptional = chapterRepository.findById(chapter.getId()).orElse(null);
        if (chapterOptional != null) {
            chapterRepository.save(chapter);
            isUpdated = true;
        }
        return isUpdated;
    }

    public boolean deleteChapter(@NotNull String chapterId) {
        boolean isDeleted = false;
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter != null) {
            ArrayList<LessonPref> lessonsPref = chapter.getLessonsPref();
            for (LessonPref lessonPref : lessonsPref) {
                lessonService.deleteLesson(lessonPref.getId());
            }
            chapterRepository.delete(chapter);
            Course course = courseRepository.findById(chapter.getCourseId()).get();
            course.getChaptersIds().remove(chapterId);

            isDeleted = true;

        }
        return isDeleted;
    }

    public ArrayList<Chapter> getAllChapters(@NotNull String courseId) {
        return chapterRepository.findAllByCourseId(courseId);
    }

    public ArrayList<LessonPref> getChapterLessons(@NotNull String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow(
                () -> new ResourceNotFoundException("Chapter", "Chapter Id", chapterId));
        return chapter.getLessonsPref();
    }

}
