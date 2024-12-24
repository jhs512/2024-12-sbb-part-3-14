package baekgwa.sbb;

import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SbbApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;

    @DisplayName("Jpa Test")
    @Test
    void testJpa() {
        Question q1 = Question
                .builder()
                .subject("sbb가 무엇인가요?")
                .content("sbb에 대해서 알고 싶습니다.")
                .createDate(LocalDateTime.now())
                .build();
        this.questionRepository.save(q1);  // 첫번째 질문 저장

        Question q2 = Question
                .builder()
                .subject("스프링부트 모델 질문입니다.")
                .content("id는 자동으로 생성되나요?")
                .createDate(LocalDateTime.now())
                .build();
        this.questionRepository.save(q2);  // 두번째 질문 저장

        List<Question> all = questionRepository.findAll();

        Assertions.assertEquals(2, all.size());
    }

}
