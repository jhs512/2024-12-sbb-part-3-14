package com.mysite.sbb;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.category.entity.Category;
import com.mysite.sbb.category.repository.CategoryRepository;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SbbApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	void t1() {
		Question q1 = new Question();
		q1.setSubject("sbb가 무엇인가요?");
		q1.setContent("sbb에 대해서 알고 싶습니다.");
		questionRepository.save(q1);

		Question q2 = new Question();
		q2.setSubject("스프링부트 모델 질문입니다.");
		q2.setContent("id는 자동으로 생성되나요?");
		questionRepository.save(q2);
	}

	@Test
	void t2() {
		List<Question> questions = questionRepository.findAll();
		assertEquals(2, questions.size());

		Question question = questions.get(0);
		assertEquals("sbb가 무엇인가요?", question.getSubject());
	}

	@Test
	void t3() {
		Optional<Question> op = questionRepository.findById(1L);

		if (op.isPresent()) {
			Question question = op.get();
			assertEquals("sbb가 무엇인가요?", question.getSubject());
		}
	}

	@Test
	void t4() {
		List<Question> questions = questionRepository.findBySubject("sbb가 무엇인가요?");
		assertEquals(1, questions.size());
		Question question = questions.get(0);
		assertEquals(1, question.getId());
	}

	@Test
	void t5() {
		List<Question> questions = questionRepository.findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
		assertEquals(1, questions.size());
		Question question = questions.get(0);
		assertEquals(1, question.getId());
	}

	@Test
	void t6() {
		List<Question> questions = questionRepository.findBySubjectLike("sbb%");
		assertEquals(1, questions.size());
		Question question = questions.get(0);
		assertEquals(1, question.getId());
	}

	@Test
	void t7() {
		Optional<Question> op = questionRepository.findById(1L);
		Question question = op.get();

		question.setSubject("수정된 제목");
		questionRepository.save(question);

		Optional<Question> op2 = questionRepository.findById(1L);
		Question question2 = op.get();

		assertEquals("수정된 제목", question2.getSubject());
	}

	@Test
	void t8() {
		assertEquals(2, questionRepository.count());
		Optional<Question> op = questionRepository.findById(2L);

		Question question = op.get();
		questionRepository.delete(question);
		assertEquals(1, questionRepository.count());
	}

	@Test
	void t9() {
		Optional<Question> op = questionRepository.findById(1L);

		Question question = op.get();

		answerRepository.save(
				Answer.builder()
						.content("네 자동으로 생성됩니다.")
						.question(question)
						.build()
		);
		answerRepository.save(
				Answer.builder()
						.content("네 자동으로 생성됩니다.")
						.question(question)
						.build()
		);

		assertEquals(2, answerRepository.count());
	}

	@Test
	@Transactional
	void t10() {
		Optional<Question> op = questionRepository.findById(1L);

		assertTrue(op.isPresent());
		Question question = op.get();

		List<Answer> answers = question.getAnswers();

		assertEquals(2, answers.size());
	}
	@Test
	void t11() {
		for (int i = 1; i<=300; i++) {
			questionRepository.save(Question.builder()
					.subject("테스트 데이터입니다:[%03d]".formatted(i))
					.content("내용무")
					.build());
		}
	}
	@Test
	void t12() {
		Category category1 = Category.builder()
				.name("질문답변").build();
		categoryRepository.save(category1);

		Category category2 = Category.builder()
				.name("게시판").build();
		categoryRepository.save(category2);

		Category category3 = Category.builder()
				.name("공지").build();
		categoryRepository.save(category3);
	}
}
