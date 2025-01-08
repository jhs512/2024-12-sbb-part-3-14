package com.ll.pratice1.domain.category.service;

import com.ll.pratice1.domain.category.Category;
import com.ll.pratice1.domain.category.repository.CategoryRepository;
import com.ll.pratice1.domain.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getCategoryAll() {
        return this.categoryRepository.findAll();
    }

    public Category getCategory(String category) {
        return this.categoryRepository.findByCategory(category);
    }

    public void create(SiteUser siteUser, String str) {
        Category category = new Category();
        category.setSiteUser(siteUser);
        category.setCategory(str);
        this.categoryRepository.save(category);
    }

    public void delete(String category){
        Category byCategory = this.categoryRepository.findByCategory(category);
        this.categoryRepository.delete(byCategory);
    }
}
