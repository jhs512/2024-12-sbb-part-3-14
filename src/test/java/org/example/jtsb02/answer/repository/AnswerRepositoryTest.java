package org.example.jtsb02.answer.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.repository.MemberRepository;
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

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("답변 등록")
    void save() {
        //given
        Member member = memberRepository.save(Member.of(
            "onlyTest",
            "onlyTest",
            "onlyTest",
            "onlyTest@gmail.com")
        );
        Question question = questionRepository.save(
            Question.of("question subject", "question content", member));
        Answer answer = Answer.of("test content", question, member);


        //when
        Answer save = answerRepository.save(answer);

        //then
        assertThat(save).isNotNull();
        assertThat(save.getId()).isEqualTo(answer.getId());
        assertThat(save.getContent()).isEqualTo(answer.getContent());
        assertThat(save.getQuestion()).isEqualTo(question);
    }

    @Test
    @DisplayName("답변 수정")
    void modify() {
        //given
        Member member = memberRepository.save(Member.of(
            "onlyTest",
            "onlyTest",
            "onlyTest",
            "onlyTest@gmail.com")
        );
        Question question = questionRepository.save(
            Question.of("question subject", "question content", member));
        Answer answer = answerRepository.save(Answer.of("answer content", question, member));

        //when
        Answer result = answerRepository.findById(answer.getId()).map(a -> a.toBuilder()
                .content("수정한 내용")
                .modifiedAt(LocalDateTime.now())
                .build())
            .orElseThrow(() -> new DataNotFoundException("Answer not found"));

        //then
        assertThat(result.getId()).isEqualTo(answer.getId());
        assertThat(result.getContent()).isEqualTo("수정한 내용");
        assertThat(result.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("답변 삭제")
    void delete() {
        //given
        Member member = memberRepository.save(Member.of(
            "onlyTest",
            "onlyTest",
            "onlyTest",
            "onlyTest@gmail.com")
        );
        Question question = questionRepository.save(
            Question.of("question subject", "question content", member));
        Answer answer = answerRepository.save(Answer.of("answer content", question, member));

        //when
        answerRepository.findById(answer.getId()).ifPresent(a -> answerRepository.delete(a));

        //then
        assertThatThrownBy(() -> answerRepository.findById(answer.getId())
            .orElseThrow(() -> new DataNotFoundException("Answer not found")))
            .isInstanceOf(DataNotFoundException.class).hasMessageContaining("Answer not found");
    }
}