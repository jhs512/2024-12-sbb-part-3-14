package org.example.sbb;

import org.example.sbb.question.QuestionService;
import org.example.sbb.user.SiteUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SbbApplicationTests {
    @Autowired
    private QuestionService questionService;

    @Test
    void 데이터주입() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            SiteUser siteUser = new SiteUser();
            siteUser.setId(i);

        }
    }
}
