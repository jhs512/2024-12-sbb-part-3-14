package org.example.jtsb02.category.repository;

import org.example.jtsb02.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
