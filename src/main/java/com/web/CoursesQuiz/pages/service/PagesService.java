package com.web.CoursesQuiz.pages.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.web.CoursesQuiz.pages.entity.Pages;
import com.web.CoursesQuiz.pages.repo.PagesRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PagesService {

    private PagesRepository pagesRepository;

    public List<Pages> getPages() {
        return pagesRepository.findAll();
    }

    public Pages getPage(String id) {
        if (pagesRepository.findById(id).isPresent()) {
            return pagesRepository.findById(id).get();
        } else {
            throw new RuntimeException("Page not found for id: " + id);
        }
    }

    public void addPage(Pages pages) {
        pages.setId(null);
        pagesRepository.save(pages);
    }

    public void updatePage(String id, Pages pages) {
        if (pagesRepository.findById(id).isPresent()) {
            Pages page = pagesRepository.findById(id).get();
            page.setAbout(pages.getAbout());
            page.setContact(pages.getContact());
            page.getContact().setEmail(pages.getContact().getEmail());
            page.getContact().setPhone(pages.getContact().getPhone());
            pagesRepository.save(page);
        } else {
            throw new RuntimeException("Page not found for id: " + id);
        }
    }

    public void deletePage(String id) {
        if (pagesRepository.findById(id).isPresent()) {
            pagesRepository.deleteById(id);
        } else {
            throw new RuntimeException("Page not found for id: " + id);
        }
    }

}
