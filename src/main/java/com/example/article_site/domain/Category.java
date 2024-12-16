package com.example.article_site.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    private String name;

    public static Category createCategory(String name) {
        Category category = new Category();
        category.name = name;
        return category;
    }
}
