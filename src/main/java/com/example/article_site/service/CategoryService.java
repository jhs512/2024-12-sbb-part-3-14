package com.example.article_site.service;

import com.example.article_site.domain.Category;
import com.example.article_site.exception.DataNotFoundException;
import com.example.article_site.form.CategoryForm;
import com.example.article_site.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.article_site.domain.Category.createCategory;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(String name) {
        Category category = createCategory(name);
        categoryRepository.save(category);
        return category;
    }

    public List<String> getCategoryNames() {
        List<Category> all = categoryRepository.findAll();
        // 테스트 겸, 하나는 무조건 있게 한다.
        if(all.isEmpty()) {
            create("전체");
            all = categoryRepository.findAll();
        }
        return all.stream().map(Category::getName).toList();
    }

    public Category findByName(String category) {
        Optional<Category> byName = categoryRepository.findByName(category);
        if(byName.isEmpty()){
            throw new DataNotFoundException("Category not found");
        }
        return byName.get();
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
}
