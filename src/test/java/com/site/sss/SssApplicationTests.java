package com.site.sss;


import com.site.sss.Category.Category;
import com.site.sss.Category.CategoryRepository;
import com.site.sss.question.Question;
import com.site.sss.question.QuestionRepository;
import com.site.sss.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SssApplicationTests {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private CategoryRepository categoryRepository;

	/*
	@Test
	void testJpa() {
		for (int i = 1; i <= 300; i++) {
			String subject = String.format("테스트 데이터입니다:[%03d]", i);
			String content = "내용무";
			Category category=categoryRepository.findById(1).get();
			this.questionService.create(subject, content,null,category);
		}
	}

	 */
}
