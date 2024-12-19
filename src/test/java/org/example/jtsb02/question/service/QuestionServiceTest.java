package org.example.jtsb02.question.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository; // 의존성 mock

    @InjectMocks
    private QuestionService questionService; // 테스트할 클래스

    @Test
    @DisplayName("createQuestion 테스트")
    void createQuestion() {
        //given: 테스트를 위한 데이터 준비
        QuestionForm questionForm = new QuestionForm();
        questionForm.setSubject("테스트 제목");
        questionForm.setContent("테스트 내용");

        // Question 객체를 생성하는 mock 설정
        Question mockQuestion = Question.builder()
            .id(1L)
            .subject(questionForm.getSubject())
            .content(questionForm.getContent())
            .createdAt(LocalDateTime.now())
            .hits(0)
            .build();

        // questionRepository.save() 메서드가 호출될 때 mockQuestion 객체를 반환하도록 설정
        when(questionRepository.save(Mockito.any(Question.class))).thenReturn(mockQuestion);

        // when: 메서드 실행
        Long questionId = questionService.createQuestion(questionForm);

        // then: 결과 검증
        assertThat(questionId).isNotNull();
        assertThat(questionId).isEqualTo(1L);
    }
}