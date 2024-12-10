package com.mysite.sbb.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.qustion.QuestionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void testJpa() {
		List<Question> q = this.questionRepository.findBySubject("sbb가 뭐임");
		q.forEach(e -> {System.out.println(e.getSubject());});

		List<Question> q2 = this.questionRepository.findBySubject("sbb가 뭐임");
		q2.forEach(e -> {System.out.println(e.getSubject());});

	}

}
