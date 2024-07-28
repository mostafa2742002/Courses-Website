package com.web.CoursesQuiz.pages.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.web.CoursesQuiz.pages.entity.Pages;
import com.web.CoursesQuiz.pages.service.PagesService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/api", produces = "application/json")
public class pagesController {

    private final PagesService pagesService;

    @GetMapping("/pages")
    public List<Pages> getPages() {
        return pagesService.getPages();
    }

    @GetMapping("/pages/{id}")
    public Pages getPage(@RequestParam String id) {
        return pagesService.getPage(id);
    }

    @PostMapping("/pages")
    public void addPage(@RequestBody Pages pages) {
        pagesService.addPage(pages);
    }

    @PutMapping("/pages/{id}")
    public void updatePage(@RequestParam String id, @RequestBody Pages pages) {
        pagesService.updatePage(id, pages);
    }

    @DeleteMapping("/pages/{id}")
    public void deletePage(@RequestParam String id) {
        pagesService.deletePage(id);
    }
}
