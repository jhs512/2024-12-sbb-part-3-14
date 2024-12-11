package baekgwa.sbb.global.test;

import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.answer.persistence.AnswerRepository;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class DevSampleDataFactory {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void addQuestionAndAnswer() {
        for(int i=1; i<=300; i++) {
            Question question = createQuestion(String.format("%d번 질문입니다!", i),
                    String.format("%d번의 상세 질문 내용 입니다!!", i));
            questionRepository.save(question);

            int randomCount = new Random().nextInt(2, 5);
            for(int j=1; j<=randomCount; j++) {
                Answer answer = createAnswer(question, String.format("%d번 질문의 %d번째 답변 입니다!", i, j));
                answerRepository.save(answer);
            }
        }
    }

    private static Question createQuestion(String subject, String content) {
        return Question
                .builder()
                .subject(subject)
                .content(content)
                .createDate(LocalDateTime.now())
                .build();
    }

    private static Answer createAnswer(Question question, String content) {
        return Answer
                .builder()
                .content(content)
                .createDate(LocalDateTime.now())
                .question(question)
                .build();
    }
}
