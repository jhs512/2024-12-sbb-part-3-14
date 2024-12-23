package org.example.jtsb02.answer.repository;

import static org.assertj.core.api.Assertions.*;

import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AnswerRepositoryTest {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("답변 등록")
    void save() {
        //given
        Question question = questionRepository.save(
            Question.of("question subject", "question content"));
        Answer answer = Answer.of("test content", question);

        //when
        Answer save = answerRepository.save(answer);

        //then
        assertThat(save).isNotNull();
        assertThat(save.getId()).isEqualTo(1L);
        assertThat(save.getContent()).isEqualTo(answer.getContent());
        assertThat(save.getQuestion()).isEqualTo(question);
    }
}