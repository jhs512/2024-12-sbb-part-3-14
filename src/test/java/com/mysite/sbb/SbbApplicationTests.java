//package com.mysite.sbb;
//
//import com.mysite.sbb.domain.answer.entity.Answer;
//import com.mysite.sbb.repository.AnswerRepository;
//import com.mysite.sbb.domain.question.entity.Question;
//import com.mysite.sbb.repository.QuestionRepository;
//import com.mysite.sbb.service.impl.QuestionServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//class SbbApplicationTests {
//
//	@Autowired
//	private QuestionRepository questionRepository;
//	@Autowired
//	private AnswerRepository answerRepository;
//
//	@Autowired
//	QuestionServiceImpl questionServiceImpl;
//
//	@Test
//	void addQuestion() {
//		Question q1 = new Question();
//		q1.setSubject("sbb가 무엇인가요?");
//		q1.setContent("sbb에 대해 알고 싶습니다.");
//		q1.setCreateDate(LocalDateTime.now());
//		this.questionRepository.save(q1); // 첫 번째 질문 저장
//
//		Question q2 = new Question();
//		q2.setSubject("스프링부트 모델 질문입니다.");
//		q2.setContent("id는 자동으로 생성되나요?");
//		q2.setCreateDate(LocalDateTime.now());
//		this.questionRepository.save(q2); // 첫 번째 질문 저장
//	}
//
//	@Test
//	void findAll(){
//		List<Question> all = this.questionRepository.findAll();
//		assertEquals(4, all.size());
//
//		Question q = all.get(0);
//		assertEquals("sbb가 무엇인가요?", q.getSubject());
//	}
//
//	@Test
//	void findContentById(){
//		Optional<Question> oq = this.questionRepository.findById(1);
//		if(oq.isPresent()) {
//			Question q = oq.get();
//			assertEquals("sbb가 무엇인가요?", q.getSubject());
//		}
//	}
//
//	@Test
//	void findSubjectById(){
//		Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
//		assertEquals(1, q.getId());
//	}
//
//	@Test
//	void findBySubjectAndContent(){
//		Question q = this.questionRepository.findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해 알고 싶습니다.");
//		assertEquals(1, q.getId());
//	}
//
//	@Test
//	void findBySubjectLike(){
//		List<Question> qList = this.questionRepository.findBySubjectLike("sbb%");
//		Question q = qList.get(0);
//		assertEquals("sbb가 무엇인가요?", q.getSubject());
//	}
//
//	@Test
//	void updateSubjectById(){
//		Optional<Question> oq = this.questionRepository.findById(1);
//		assertTrue(oq.isPresent());
//		Question q = oq.get();
//		q.setSubject("수정된 제목");
//		this.questionRepository.save(q);
//	}
//
//	@Test
//	void deleteById(){
//		assertEquals(2, this.questionRepository.count());
//		Optional<Question> oq = this.questionRepository.findById(1);
//		assertTrue(oq.isPresent());
//		Question q = oq.get();
//		this.questionRepository.delete(q);
//		assertEquals(1, this.questionRepository.count());
//	}
//
//	@Test
//	void addAnswer(){
//		Optional<Question> oq = this.questionRepository.findById(2);
//		assertTrue(oq.isPresent());
//		Question q = oq.get();
//
//		Answer a = new Answer();
//		a.setContent("네 자동으로 생성됩니다.");
//		a.setQuestion(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
//		a.setCreateDate(LocalDateTime.now());
//		this.answerRepository.save(a);
//	}
//
//	@Test
//	void findAnswerById(){
//		Optional<Answer> oa = this.answerRepository.findById(1);
//		assertTrue(oa.isPresent());
//		Answer a = oa.get();
//		assertEquals(2, a.getQuestion().getId());
//	}
//
//	@Transactional
//	@Test
//	void findAnswerByQuestionId(){
//		Optional<Question> oq = this.questionRepository.findById(2);
//		assertTrue(oq.isPresent());
//		Question q = oq.get();
//
//		List<Answer> answerList = q.getAnswerList();
//
//		assertEquals(1, answerList.size());
//		assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
//	}
//
//	@Test
//	void addTestData() {
//		for (int i = 0; i <= 300; i++) {
//			String subject = String.format("테스트 데이터입니다:[%03d]", i);
//			String content = "내용무";
//			questionServiceImpl.create(subject, content, null);
//		}
//	}
//
//
//}
