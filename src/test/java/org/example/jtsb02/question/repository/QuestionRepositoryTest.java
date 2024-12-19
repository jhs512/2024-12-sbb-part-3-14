package org.example.jtsb02.question.repository;

import static org.assertj.core.api.Assertions.*;

import org.example.jtsb02.question.entity.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("질문 등록")
    void save() {
        //given
        Question question = Question.of("제목1", "내용1");

        //when
        Question save = questionRepository.save(question);

        //then
        assertThat(save).isNotNull();
        assertThat(save.getId()).isEqualTo(1L);
        assertThat(save.getSubject()).isEqualTo("제목1");
        assertThat(save.getContent()).isEqualTo("내용1");
        assertThat(save.getHits()).isEqualTo(0);
    }
}