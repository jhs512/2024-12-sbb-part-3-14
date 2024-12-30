package com.mysite.sbb.category.service;

import com.mysite.sbb.category.entity.Category;
import com.mysite.sbb.category.repository.CategoryRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category findCategory(String categoryName) {
        Optional<Category> categoryOptional = this.categoryRepository.findByCategoryName(categoryName);
        if(categoryOptional.isPresent()) {
            return categoryOptional.get();
        } else {
            throw new DataNotFoundException("category not found");
        }
    }

    public List<Category> getList() {
        return this.categoryRepository.findAll();
    }
}
