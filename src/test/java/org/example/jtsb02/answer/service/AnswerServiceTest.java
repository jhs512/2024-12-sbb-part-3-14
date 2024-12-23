package org.example.jtsb02.answer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.repository.AnswerRepository;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    @DisplayName("질문 등록")
    void createAnswer() {
        //given
        Question question = createQuestion(1L);
        AnswerForm answerForm = createAnswerForm("answer content");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        ArgumentCaptor<Answer> captor = ArgumentCaptor.forClass(Answer.class);

        //when
        answerService.createAnswer(1L, answerForm);
        verify(answerRepository, times(1)).save(captor.capture());
        Answer save = captor.getValue();

        //then
        assertThat(save.getContent()).isEqualTo(answerForm.getContent());
        assertThat(save.getQuestion()).isEqualTo(question);
        assertThat(save.getQuestion().getId()).isEqualTo(1L);
    }

    private AnswerForm createAnswerForm(String content) {
        AnswerForm answerForm = new AnswerForm();
        answerForm.setContent(content);
        return answerForm;
    }

    private Answer createAnswer(Long id, AnswerForm answerForm, Question question) {
        return Answer.builder()
            .id(id)
            .content(answerForm.getContent())
            .createdAt(LocalDateTime.now())
            .question(question)
            .build();
    }

    private Question createQuestion(Long id) {
        return Question.builder()
            .id(id)
            .subject("test subject")
            .content("test content")
            .createdAt(LocalDateTime.now())
            .hits(1)
            .build();
    }
}