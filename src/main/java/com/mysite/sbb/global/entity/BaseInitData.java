package com.mysite.sbb.global.entity;

import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.category.entity.Category;
import com.mysite.sbb.category.repository.CategoryRepository;
import com.mysite.sbb.category.service.CategoryService;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final AnswerRepository answerRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            if(this.categoryRepository.count() < 3) {
                self.work3();
            }
//            self.work1();
            self.work2();
        };
    }

//    @Transactional
//    public void work1() {
//        if (questionRepository.count() > 0) return;
//
//        Question q1 = new Question();
//        q1.setSubject("sbb가 무엇인가요?");
//        q1.setContent("sbb에 대해서 알고 싶습니다.");
//        q1.setCreateDate(LocalDateTime.now());
//        this.questionRepository.save(q1);  // 첫번째 질문 저장
//
//        Question q2 = new Question();
//        q2.setSubject("스프링부트 모델 질문입니다.");
//        q2.setContent("id는 자동으로 생성되나요?");
//        q2.setCreateDate(LocalDateTime.now());
//        this.questionRepository.save(q2);  // 두번째 질문 저장
//
//        Answer a = new Answer();
//        a.setContent("네 자동으로 생성됩니다.");
//        a.setQuestion(q2);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
//        a.setCreateDate(LocalDateTime.now());
//        this.answerRepository.save(a);
//    }

    @Transactional
    public void work2() {
        if (questionRepository.count() > 300) return;

        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            Category category = this.categoryService.findCategory("question");
            this.questionService.createQuestion(subject, content, null, category);
        }
    }

    @Transactional
    public void work3() {
        // 카테고리 3개 만들기
        Category category1 = new Category();
        category1.setCategoryName("question");
        category1.setCategoryKorName("질문/답변");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setCategoryName("class");
        category2.setCategoryKorName("강의");
        categoryRepository.save(category2);

        Category category3 = new Category();
        category3.setCategoryName("freeBoard");
        category3.setCategoryKorName("자유게시판");
        categoryRepository.save(category3);
    }
}