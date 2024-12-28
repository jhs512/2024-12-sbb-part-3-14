package com.mysite.sbb.domain.category;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(@NotEmpty(message = "카테고리는 필수항목입니다.") String category);
}
