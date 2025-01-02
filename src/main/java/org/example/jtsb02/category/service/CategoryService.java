package org.example.jtsb02.category.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.category.dto.CategoryDto;
import org.example.jtsb02.category.entity.Category;
import org.example.jtsb02.category.form.CategoryForm;
import org.example.jtsb02.category.repository.CategoryRepository;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void createCategory(CategoryForm categoryForm) {
        categoryRepository.save(Category.of(categoryForm.getName()));
    }

    public CategoryDto getCategory(Long id) {
        return CategoryDto.fromCategory(categoryRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("category not found")));
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(CategoryDto::fromCategory).toList();
    }
}
