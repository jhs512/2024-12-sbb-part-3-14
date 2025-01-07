package com.mysite.sbb.category.repository;

import com.mysite.sbb.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);

    //  2025-01-04 : 카테고리 로직 추가 반영 : 테이블에 유사 카테고리 유무 확인
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:categoryName%")
    List<Category> findSimilarCategories(@Param("categoryName") String categoryName);
}
