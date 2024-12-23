package org.example.jtsb02.question.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    @DisplayName("질문 등록")
    void createQuestion() {
        //given: 테스트를 위한 데이터 준비
        QuestionForm questionForm = createQuestionForm("테스트 제목", "테스트 내용");

        // Question 객체를 생성하는 mock 설정
        Question mockQuestion = createQuestion(1L, questionForm);

        // questionRepository.save() 메서드가 호출될 때 mockQuestion 객체를 반환하도록 설정
        when(questionRepository.save(Mockito.any(Question.class))).thenReturn(mockQuestion);

        // when: 메서드 실행
        Long questionId = questionService.createQuestion(questionForm);

        // then: 결과 검증
        assertThat(questionId).isNotNull();
        assertThat(questionId).isEqualTo(1L);
    }

    @Test
    @DisplayName("질문 목록 조회")
    void getQuestions() {
        //given
        Question question1 = createQuestion(1L, createQuestionForm("제목1", "내용1"));
        Question question2 = createQuestion(2L, createQuestionForm("제목2", "내용2"));
        List<Question> questions = List.of(question1, question2);
        when(questionRepository.findAll()).thenReturn(questions);

        //when
        List<QuestionDto> result = questionService.getQuestions();

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.getFirst().getSubject()).isEqualTo("제목1");
        assertThat(result.getFirst().getContent()).isEqualTo("내용1");
        assertThat(result.get(1).getSubject()).isEqualTo("제목2");
        assertThat(result.get(1).getContent()).isEqualTo("내용2");
    }

    @Test
    @DisplayName("질문 조회시 조회수 증가")
    void getQuestion() {
        //given
        Question question = createQuestion(1L, createQuestionForm("제목1", "내용1"));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(Mockito.any(Question.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        //when
        QuestionDto result = questionService.getQuestionWithHitsCount(1L);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubject()).isEqualTo("제목1");
        assertThat(result.getContent()).isEqualTo("내용1");
        assertThat(question.getHits()).isEqualTo(0);
        assertThat(result.getHits()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 질문 ID로 조회")
    void getQuestion_notFound() {
        //given
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> questionService.getQuestion(1L))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage("Question not found");
    }

    @Test
    @DisplayName("질문 수정")
    void modifyQuestion() {
        //given
        Question question = createQuestion(1L, createQuestionForm("제목1", "내용1"));
        QuestionForm modifyQuestionForm = createQuestionForm("수정한 제목1", "수정한 내용1");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(Mockito.any(Question.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);

        //when
        questionService.modifyQuestion(1L, modifyQuestionForm);
        verify(questionRepository, times(1)).save(captor.capture());
        Question save = captor.getValue();

        //then
        assertThat(save.getId()).isEqualTo(1L);
        assertThat(save.getSubject()).isEqualTo("수정한 제목1");
        assertThat(save.getContent()).isEqualTo("수정한 내용1");
    }

    @Test
    @DisplayName("질문 삭제")
    void deleteQuestion() {
        //given
        Question question = createQuestion(1L, createQuestionForm("제목1", "내용1"));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        //when
        questionService.deleteQuestion(1L);

        //then
        verify(questionRepository, times(1)).delete(question);
    }

    private QuestionForm createQuestionForm(String subject, String content) {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setSubject(subject);
        questionForm.setContent(content);
        return questionForm;
    }

    private Question createQuestion(Long id, QuestionForm questionForm) {
        return Question.builder()
            .id(id)
            .subject(questionForm.getSubject())
            .content(questionForm.getContent())
            .createdAt(LocalDateTime.now())
            .hits(0)
            .answers(new ArrayList<>())
            .build();
    }
}