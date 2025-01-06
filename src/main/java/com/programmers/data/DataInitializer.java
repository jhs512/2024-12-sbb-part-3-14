package com.programmers.data;

import com.programmers.answer.AnswerService;
import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.question.QuestionService;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import com.programmers.recommend.answerRecommend.ARecommendService;
import com.programmers.recommend.questionRecommend.QRecommendService;
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
    private final AnswerService answerService;
    private final ARecommendService arecommendService;
    private final QRecommendService qRecommendService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        siteUserService.save(SignupDto.builder()
                        .userName("asdasd")
                        .password("asd")
                        .passwordConfirmation("asd")
                        .email("email")
                .build());
        // 가짜 Principal 생성
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "asdasd";  // 가짜 사용자 이름
            }
        };
        for (int i = 1; i <= 60; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = """
            마크다운 적용
            
            ## 마크다운 문법으로 작성.
            * 리스트1
            * 리스트 2
            * 리스트 3
            파이선 홈페이지는 [http://www.python.org](http://www.python.org) 입니다.
            """;
            questionService.createQuestion(new QuestionRegisterRequestDto(subject, content), principal.getName());
        }
        for (int i = 1; i <= 60; i++) {
            String content = "테스트용 댓글" + i;
            answerService.createAnswer(60L, new AnswerRegisterRequestDto(content), principal.getName());
        }

        for (int i = 40; i <= 50; i++) {
            arecommendService.recommend(60L, (long) i, principal.getName());
        }
    }
}
