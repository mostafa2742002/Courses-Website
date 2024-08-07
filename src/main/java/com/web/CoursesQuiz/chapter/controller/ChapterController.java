package com.web.CoursesQuiz.chapter.controller;

import java.util.ArrayList;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.CoursesQuiz.chapter.entity.Chapter;
import com.web.CoursesQuiz.chapter.service.ChapterService;
import com.web.CoursesQuiz.course.dto.LessonPref;
import com.web.CoursesQuiz.dto.ResponseDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/api/chapter", produces = { MediaType.APPLICATION_JSON_VALUE })
public class ChapterController {

    private final ChapterService chapterService;

    // CRUD operations for Chapter entity

    @PostMapping
    public ResponseEntity<ResponseDto> createChapter(@RequestBody @NotNull Chapter chapter) {
        return chapterService.addChapter(chapter);
    }

    @GetMapping("/{chapterId}")
    public ResponseEntity<Chapter> getChapter(@PathVariable String chapterId) {
        Chapter chapter = chapterService.getChapter(chapterId);
        return ResponseEntity.ok(chapter);
    }

    @PutMapping
    public ResponseEntity<Boolean> updateChapter(@Valid @RequestBody Chapter chapter) {
        boolean isUpdated = chapterService.updateChapter(chapter);
        return ResponseEntity.ok(isUpdated);
    }

    @DeleteMapping("/{chapterId}")
    public ResponseEntity<Boolean> deleteChapter(@PathVariable String chapterId) {
        boolean isDeleted = chapterService.deleteChapter(chapterId);
        return ResponseEntity.ok(isDeleted);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ArrayList<Chapter>> getAllChapters(@PathVariable String courseId) {
        ArrayList<Chapter> chapters = chapterService.getAllChapters(courseId);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/{chapterId}/lessons")
    public ResponseEntity<ArrayList<LessonPref>> getChapterLessons(@PathVariable String chapterId) {
        ArrayList<LessonPref> lessons = chapterService.getChapterLessons(chapterId);
        return ResponseEntity.ok(lessons);
    }
}
