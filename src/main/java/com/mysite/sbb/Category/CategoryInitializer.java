package com.mysite.sbb.Category;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void initCategories() {
        if (categoryRepository.count() == 0) { // 데이터베이스에 카테고리가 없을 때만 추가
            Category category1 = new Category();
            category1.setName("질문답변");
            category1.setDescription("질문과 답변 게시판입니다.");
            categoryRepository.save(category1);

            Category category2 = new Category();
            category2.setName("강좌");
            category2.setDescription("강좌 및 학습자료 게시판입니다.");
            categoryRepository.save(category2);

            Category category3 = new Category();
            category3.setName("자유게시판");
            category3.setDescription("자유롭게 글을 작성할 수 있는 게시판입니다.");
            categoryRepository.save(category3);

            System.out.println("카테고리가 초기화되었습니다.");
        }
    }
}