package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SbbApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Test
	void insertQuestion() {
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

	// findAll() 테스트
	@Test
	void finaAll() {
		List<Question> all = this.questionRepository.findAll();
		assertEquals(2, all.size());

		Question q = all.get(0);
		assertEquals("sbb가 무엇인가요?", q.getSubject());
	}

	// findById() 테스트
	@Test
	void findById() {
		Optional<Question> oq = this.questionRepository.findById(1);
		if(oq.isPresent()){
			Question q = oq.get();
			assertEquals("sbb가 무엇인가요?", q.getSubject());
		}
	}

	// findByXXX() 테스트 : XXX 는 필드명
	@Test
	void findByXXX() {
		Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
		assertEquals(1, q.getId());
	}

	// findByXXX() 테스트 : XXX 는 여러 필드명
	@Test
	void findByXXXAndXXX() {
		Question q = this.questionRepository.findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
		assertEquals(1, q.getId());
	}

	// findByXXXLike() 테스트 : XXX 와 문자열 비교 (Like 연산)
	@Test
	void findByXXXLike() {
		List<Question> qList = this.questionRepository.findBySubjectLike("sbb%");
		assertEquals("sbb가 무엇인가요?", qList.get(0).getSubject());
	}

	// 수정
	@Test
	void update(){
		Optional<Question> oq = this.questionRepository.findById(1);
		assertTrue(oq.isPresent()) ;
		Question q = oq.get();
		q.setSubject("수정된 제목");
		this.questionRepository.save(q);
	}

	//삭제
	@Test
	void delete() {
		assertEquals(2, this.questionRepository.count());
		Optional<Question> oq = this.questionRepository.findById(1);
		assertTrue(oq.isPresent()) ;
		Question q = oq.get();
		this.questionRepository.delete(q);
		assertEquals(1, this.questionRepository.count());
	}

	// 답변 데이터 저장
	@Test
	void insertAnswer() {
		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		Answer a = new Answer();
		a.setContent("네 자동으로 생성됩니다.");
		a.setQuestion(q);
		a.setCreateDate(LocalDateTime.now());
		this.answerRepository.save(a);
	}

	// 답변 데이터 조회
	@Test
	void selectAnswer() {
		Optional<Answer> oa = this.answerRepository.findById(1);
		assertTrue(oa.isPresent());
		Answer a = oa.get();
		assertEquals(2, a.getQuestion().getId());
	}

	// 질문에 대한 답변들 찾기
	// @Transactional 사용 이유
	// : getAnswerList() 는 lazy Init 방식으로 동작해서 DB와의 연결이 필요한데
	//   findById() 실행 후에 DB 세션 연결이 끊어져서 getAnswerList는 오류 발생
	//   -> @Transactional 을 통해 함수가 모두 실행될때까지 DB 세션 연결 유지
	@Transactional
	@Test
	void findAnswersFromQuestion() {
		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		List<Answer> answerList = q.getAnswerList();

		assertEquals(1, answerList.size());
		assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
	}
}
