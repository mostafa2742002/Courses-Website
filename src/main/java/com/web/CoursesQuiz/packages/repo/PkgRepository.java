package com.web.CoursesQuiz.packages.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.web.CoursesQuiz.lesson.entity.Lesson;
import com.web.CoursesQuiz.packages.entity.Pkg;

import org.springframework.data.domain.Pageable;
import java.util.*;


@Repository
public interface PkgRepository extends MongoRepository<Pkg, String> {
    
        Pkg findByName(String name);
}
