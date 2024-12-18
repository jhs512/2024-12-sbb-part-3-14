package com.mysite.sbb.category;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category create(String title) {
        Category category = Category.builder().title(title).build();
        return categoryRepository.save(category);
    }

    public List<Category> getCategoryList(){
        return categoryRepository.findAll();
    }

    public Category getCategory(int id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if(optionalCategory.isEmpty()) {
            throw new DataNotFoundException("데이터가 존재하지 않습니다.");
        }
        return optionalCategory.get();
    }
}
