package com.web.CoursesQuiz.packages.entity;

import java.util.ArrayList;

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

    private ArrayList<String> description;

    private Integer priceForEgypt;

    private Integer priceForNonEgypt;

    private String image;

    private int durationByMonths; 

}
