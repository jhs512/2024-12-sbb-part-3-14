package org.example.jtsb02.question.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    @DisplayName("createQuestion 테스트")
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
    @DisplayName("getQuestions 테스트")
    void getQuestions() {
        //given
        QuestionForm questionForm1 = createQuestionForm("제목1", "내용1");
        QuestionForm questionForm2 = createQuestionForm("제목2", "내용2");
        Question question1 = createQuestion(1L, questionForm1);
        Question question2 = createQuestion(2L, questionForm2);
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
    @DisplayName("getQuestion, addHits 테스트")
    void getQuestion() {
        //given
        QuestionForm questionForm = createQuestionForm("제목1", "내용1");
        Question question = createQuestion(1L, questionForm);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(Mockito.any(Question.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        //when
        QuestionDto result = questionService.getQuestion(1L);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubject()).isEqualTo("제목1");
        assertThat(result.getContent()).isEqualTo("내용1");
        assertThat(result.getHits()).isEqualTo(1);
    }

    @Test
    @DisplayName("modifyQuestion 테스트")
    void modifyQuestion() {
        //given
        QuestionForm questionForm = createQuestionForm("제목1", "내용1");
        QuestionForm modifyQuestionForm = createQuestionForm("수정한 제목1", "수정한 내용1");
        Question question = createQuestion(1L, questionForm);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(Mockito.any(Question.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);

        //when
        questionService.modifyQuestion(1L, modifyQuestionForm);

        //then
        verify(questionRepository, times(1)).save(captor.capture());
        Question save = captor.getValue();
        assertThat(save.getId()).isEqualTo(1L);
        assertThat(save.getSubject()).isEqualTo("수정한 제목1");
        assertThat(save.getContent()).isEqualTo("수정한 내용1");
    }

    @Test
    @DisplayName("deleteQuestion 테스트")
    void deleteQuestion() {
        //given
        QuestionForm questionForm = createQuestionForm("제목1", "내용1");
        Question question = createQuestion(1L, questionForm);
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
            .build();
    }
}