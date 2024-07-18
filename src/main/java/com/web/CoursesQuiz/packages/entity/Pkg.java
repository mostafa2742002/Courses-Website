package com.web.CoursesQuiz.packages.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "packages")
public class Pkg {

    @Id
    private String id;

    private String name;

    private String description;

    private Double price;

}
