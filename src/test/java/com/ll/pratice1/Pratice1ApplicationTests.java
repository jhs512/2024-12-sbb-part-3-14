package com.ll.pratice1;

import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.question.repository.QuestionRepository;
import com.ll.pratice1.domain.question.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class Pratice1ApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;


    @Test
    void test1() {
        Question q1 = new Question();
        q1.setSubject("안녕하세요");
        q1.setContent("1234");
        q1.setCreateDate(LocalDateTime.now());
        questionRepository.save(q1);
    }

    @Transactional
    @Test
    void test2() {
        Optional<Question> oq = this.questionRepository.findById(1);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        List<Answer> answerList = q.getAnswerList();

        assertEquals(1, answerList.size());
        assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
    }

    @Test
    void test3() {
        for (int i = 1; i <= 150; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.questionService.create(subject, content, null);
        }
    }
}
