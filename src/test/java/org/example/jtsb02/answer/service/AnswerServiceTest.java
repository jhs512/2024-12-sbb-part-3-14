package org.example.jtsb02.answer.service;

import static org.assertj.core.api.Assertions.*;
import static org.example.util.TestHelper.createAnswer;
import static org.example.util.TestHelper.createAnswerForm;
import static org.example.util.TestHelper.createCategory;
import static org.example.util.TestHelper.createMember;
import static org.example.util.TestHelper.createQuestion;
import static org.example.util.TestHelper.createQuestionForm;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.repository.AnswerRepository;
import org.example.jtsb02.category.entity.Category;
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
    void createAnswerTest() {
        //given
        Category category = createCategory();
        Question question = createQuestion(1L, createQuestionForm(1L, "test subject", "test content"), category);
        AnswerForm answerForm = createAnswerForm("answer content");
        Answer answer = createAnswer(answerForm, question);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        //when
        Long result = answerService.createAnswer(1L, answerForm, createMember());

        //then
        assertThat(result).isEqualTo(answer.getId());
    }

    @Test
    @DisplayName("답변 조회")
    void getAnswerTest() {
        //given
        Category category = createCategory();
        Question question = createQuestion(1L, createQuestionForm(1L, "test subject", "test content"), category);
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
    void modifyAnswerTest() {
        //given
        Category category = createCategory();
        Question question = createQuestion(1L, createQuestionForm(1L, "test subject", "test content"), category);
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
    void deleteAnswerTest() {
        //given
        Category category = createCategory();
        Question question = createQuestion(1L, createQuestionForm(1L, "test subject", "test content"), category);
        AnswerForm answerForm = createAnswerForm("answer content");
        Answer answer = createAnswer(answerForm, question);

        //when
        answerService.deleteAnswer(AnswerDto.fromAnswer(answer));

        //then
        verify(answerRepository, times(1)).delete(ArgumentMatchers.any(Answer.class));
    }
}