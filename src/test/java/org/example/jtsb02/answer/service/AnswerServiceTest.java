package org.example.jtsb02.answer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.repository.AnswerRepository;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerService answerService;

    @Test
    @DisplayName("답변 등록")
    void createAnswer() {
        //given
        Question question = createQuestion();
        AnswerForm answerForm = createAnswerForm("answer content");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        ArgumentCaptor<Answer> captor = ArgumentCaptor.forClass(Answer.class);

        //when
        answerService.createAnswer(1L, answerForm, MemberDto.builder().id(1L).build());
        verify(answerRepository, times(1)).save(captor.capture());
        Answer save = captor.getValue();

        //then
        assertThat(save.getContent()).isEqualTo(answerForm.getContent());
        assertThat(save.getQuestion()).isEqualTo(question);
        assertThat(save.getQuestion().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("답변 조회")
    void getAnswer() {
        //given
        Question question = createQuestion();
        AnswerForm answerForm = createAnswerForm("answer content");
        Answer answer = createAnswer(answerForm, question);

        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        //when
        AnswerDto result = answerService.getAnswer(1L);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo(answerForm.getContent());
        assertThat(result.getQuestion().getId()).isEqualTo(question.getId());
    }

    @Test
    @DisplayName("답변 수정")
    void modifyAnswer() {
        //given
        Question question = createQuestion();
        AnswerForm answerForm = createAnswerForm("answer content");
        AnswerForm modifyForm = createAnswerForm("modify content");
        Answer answer = createAnswer(answerForm, question);

        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        ArgumentCaptor<Answer> captor = ArgumentCaptor.forClass(Answer.class);

        //when
        answerService.modifyAnswer(1L, modifyForm);
        verify(answerRepository, times(1)).save(captor.capture());
        Answer save = captor.getValue();

        //then
        assertThat(save).isNotNull();
        assertThat(save.getId()).isEqualTo(1L);
        assertThat(save.getContent()).isEqualTo(modifyForm.getContent());
    }

    @Test
    @DisplayName("답변 삭제")
    void deleteAnswer() {
        //given
        Question question = createQuestion();
        AnswerForm answerForm = createAnswerForm("answer content");
        Answer answer = createAnswer(answerForm, question);

        //when
        answerService.deleteAnswer(AnswerDto.fromAnswer(answer));

        //then
        verify(answerRepository, times(1)).delete(ArgumentMatchers.any(Answer.class));
    }

    private AnswerForm createAnswerForm(String content) {
        AnswerForm answerForm = new AnswerForm();
        answerForm.setContent(content);
        return answerForm;
    }

    private Answer createAnswer(AnswerForm answerForm, Question question) {
        return Answer.builder()
            .id(1L)
            .content(answerForm.getContent())
            .createdAt(LocalDateTime.now())
            .question(question)
            .author(Member.builder()
                .id(1L)
                .memberId("onlyTest")
                .nickname("onlyTest")
                .password("onlyTest")
                .email("onlyTest@gmail.com")
                .build())
            .build();
    }

    private Question createQuestion() {
        return Question.builder()
            .id(1L)
            .subject("test subject")
            .content("test content")
            .createdAt(LocalDateTime.now())
            .hits(1)
            .author(Member.builder()
                .id(1L)
                .memberId("onlyTest")
                .nickname("onlyTest")
                .password("onlyTest")
                .email("onlyTest@gmail.com")
                .build())
            .build();
    }
}