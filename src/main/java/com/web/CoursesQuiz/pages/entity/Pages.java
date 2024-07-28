package com.web.CoursesQuiz.pages.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pages")
public class Pages {

    @Id
    private String id;

    private String about;

    private String terms;

    private Contact contact;

    @Data
    public class Contact {
        private String email;
        private String phone;
    }
}
