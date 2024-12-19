package com.programmers.data;

import com.programmers.answer.AnswerService;
import com.programmers.question.QuestionService;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import com.programmers.user.SiteUserService;
import com.programmers.user.dto.SignupDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final QuestionService questionService;
    private final SiteUserService siteUserService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        siteUserService.save(SignupDto.builder()
                        .userName("user")
                        .password("password")
                        .passwordConfirmation("password")
                        .email("email")
                .build());
        // 가짜 Principal 생성
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "user";  // 가짜 사용자 이름
            }
        };
        for (int i = 1; i <= 60; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            questionService.createQuestion(new QuestionRegisterRequestDto(subject, content), principal);
        }
    }
}
