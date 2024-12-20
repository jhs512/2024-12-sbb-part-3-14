package com.mysite.sbb;

import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.entity.Answer;
import com.mysite.sbb.entity.Question;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.question.service.QuestionService;
import com.mysite.sbb.entity.SiteUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("테이블 생성 테스트 ")
    void CreateTable (){
        Question question = new Question();
        Answer answer = new Answer();
        //SiteUser siteUser = new SiteUser();
    }

    @Test
    @DisplayName("질문테이블에 값 추가")
    void InsertQuestion (){
        Question q1 = new Question();
        q1.setSubject("연방군 MS의 ROOT에 대해 알고싶습니다.");
        q1.setContent("RX-78-2에 대해서 알고 싶습니다.");
        q1.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q1);  // 첫번째 질문 저장

        Question q2 = new Question();
        q2.setSubject("RX-78-2의 파일럿은?");
        q2.setContent("RX-78-2의 파일럿은 왜 취급이 박한가");
        q2.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q2);  // 첫번째 질문 저장
    }

    @Test
    void SelectQuestionWithSubjectAndContent() {
        Question q = this.questionRepository.findBySubjectAndContent(
                "RX-78-2의 파일럿은?", "RX-78-2의 파일럿은 왜 취급이 박한가");
        assertEquals(2, q.getId());
    }

    @Test
    @DisplayName("질문테이블에 값 삭제")
    void DeleteQuestion() {
        assertEquals(2, this.questionRepository.count());
        Optional<Question> oq = this.questionRepository.findById(1);
        assertTrue(oq.isPresent());
        Question q = oq.get();
        this.questionRepository.delete(q);
        assertEquals(1, this.questionRepository.count());
    }

    @Test
    @DisplayName("질문테이블에 값 추가")
    void InsertAnswer (){
        Optional<Question> oq = this.questionRepository.findById(2);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        Answer a = new Answer();
        a.setContent("소년병 출신이라.");
        a.setQuestion(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
        a.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(a);
    }

    @Test
    @DisplayName("페이징용 테스트 데이터 생성")
    void testJPA() {
        for (int i = 0; i < 300; i++) {
            String s = String.format("테스트 데이터 : [%03d]",i+1);
            String c = "no contents";

            this.questionService.create(s,c,null);
        }
    }
}
