package com.mysite.sbb.category.repository;

import com.mysite.sbb.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
