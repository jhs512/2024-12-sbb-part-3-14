package com.ll.pratice1.domain.category.repository;

import com.ll.pratice1.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByCategory(String category);
}
