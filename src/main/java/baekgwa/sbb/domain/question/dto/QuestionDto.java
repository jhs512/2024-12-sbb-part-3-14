package baekgwa.sbb.domain.question.dto;

import baekgwa.sbb.model.answer.entity.Answer;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class QuestionDto {

    @Getter
    public static class DetailInfo {
        private final Integer id;
        private final String subject;
        private final String content;
        private final List<Answer> answerList;
        private final LocalDateTime createDate;

        @Builder
        private DetailInfo(Integer id, String subject, String content, List<Answer> answerList,
                LocalDateTime createDate) {
            this.id = id;
            this.subject = subject;
            this.content = content;
            this.answerList = answerList;
            this.createDate = createDate;
        }
    }

    @Getter
    public static class MainInfo {
        private final Integer id;
        private final String subject;
        private final LocalDateTime createDate;

        @Builder
        private MainInfo(Integer id, String subject, LocalDateTime createDate) {
            this.id = id;
            this.subject = subject;
            this.createDate = createDate;
        }
    }
}
