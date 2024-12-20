package com.kkd.sbb;

import com.kkd.sbb.answer.Answer;
import com.kkd.sbb.answer.AnswerRepository;
import com.kkd.sbb.answer.AnswerService;
import com.kkd.sbb.comment.CommentService;
import com.kkd.sbb.question.Question;
import com.kkd.sbb.question.QuestionRepository;
import com.kkd.sbb.question.QuestionService;
import com.kkd.sbb.user.SiteUser;
import com.kkd.sbb.user.UserService;
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

class SbbApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Test
    void testJpa1() {
        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해서 알고 싶습니다.");
        q1.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q1);  // 첫번째 질문 저장

        Question q2 = new Question();
        q2.setSubject("스프링부트 모델 질문입니다.");
        q2.setContent("id는 자동으로 생성되나요?");
        q2.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q2);  // 두번째 질문 저장
    }

    @Test
    void testJpa2() {
        List<Question> all = this.questionRepository.findAll();
        assertEquals(2, all.size());

        Question q = all.get(0);
        assertEquals("sbb가 무엇인가요?", q.getSubject());
    }

    @Test
    void testJpa3(){
        Optional<Question> oq = this.questionRepository.findById(1);
        if(oq.isPresent()){
            Question q = oq.get();
            assertEquals("sbb가 무엇인가요?", q.getSubject());
        }
    }

    @Test
    void testJpa4(){
        Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
        assertEquals(1, q.getId());
    }

    @Test
    void testJpa5(){
        Question q = this.questionRepository.findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
        assertEquals(1, q.getId());
    }

    @Test
    void testJpa6(){
        List<Question> qList = this.questionRepository.findBySubjectLike("%sbb%");

        Question q = qList.get(0);
        assertEquals("sbb가 무엇인가요?", q.getSubject());
    }

    @Test
    void testJpa7(){
        Optional<Question> oq = this.questionRepository.findById(1);

        assertTrue(oq.isPresent());
        Question q = oq.get();
        q.setSubject("수정된 제목");
        this.questionRepository.save(q);
    }

    @Test
    void testJpa8(){
        assertEquals(2,this.questionRepository.count());
        Optional<Question> oq = this.questionRepository.findById(1);
        assertTrue(oq.isPresent());
        Question q = oq.get();
        this.questionRepository.delete(q);
        assertEquals(1, questionRepository.count());
    }

    @Test
    void testJpa9() {
        Optional<Question> oq = this.questionRepository.findById(2);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        Answer a = new Answer();
        a.setContent("네 자동으로 생성됩니다.");
        a.setQuestion(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
        a.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(a);
    }

    @Test
    void testJpa10() {
        Optional<Answer> oq = this.answerRepository.findById(1);
        assertTrue(oq.isPresent());
        Answer a = oq.get();
        assertEquals(2, a.getQuestion().getId());
    }

    @Transactional
    @Test
    void testJpa11() {

        Optional<Question> oq = this.questionRepository.findById(2);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        List<Answer> answerList = q.getAnswerList();

        assertEquals(1, answerList.size());
        assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());

    }

    @Test
    void testJpa12() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.questionService.create(subject, content, null);
        }
    }

    @Transactional
    @Test
    void testJpa13() {
        List<Question> questionLst = this.questionService.getList();
        Question question = questionLst.get(questionLst.size() - 1);
//		SiteUser user = this.userService.create("temp", "temp@temp.com", "1234");
        SiteUser user = this.userService.getUser("kkd");
        this.commentService.create("테스트 질문 댓글", question, null, user);
        Answer answer = this.answerService.create(question, "테스트 답변", user);
        this.commentService.create("테스트 답변 댓글", question, answer, user);
    }
}
