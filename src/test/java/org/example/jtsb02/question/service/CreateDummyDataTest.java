package org.example.jtsb02.question.service;

import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.service.MemberService;
import org.example.jtsb02.question.form.QuestionForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CreateDummyDataTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MemberService memberService;

    @Test
    void createDummyData() {
        MemberDto member = memberService.getMember("dummy");

        for(int i = 0; i <= 300; i++) {
            String subject = String.format("테스트 데이터 입니다:[%03d]", i);
            String content = "내용무";
            questionService.createQuestion(createQuestionForm(subject, content), member);
        }

    }

    private QuestionForm createQuestionForm(String subject, String content) {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setSubject(subject);
        questionForm.setContent(content);
        return questionForm;
    }
}
