package com.mysite.sbb.catrgory;


import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    public void create(String content ) {
        Category category = new Category();
        category.setName(content);
        this.categoryRepository.save(category);

    }
    public List<Category> getCategorys(){
        return this.categoryRepository.findAll();
    }
    public Category getCategory(int id){
        return this.categoryRepository.findById(id);
    }
}
