package com.programmers.data;

import com.programmers.question.QuestionService;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final QuestionService questionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 1; i <= 60; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            questionService.createQuestion(new QuestionRegisterRequestDto(subject, content));
        }
    }
}
