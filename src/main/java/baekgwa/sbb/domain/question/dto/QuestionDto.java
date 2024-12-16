package baekgwa.sbb.domain.question.dto;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import baekgwa.sbb.domain.answer.dto.AnswerDto.AnswerDetailInfo;
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
        private final LocalDateTime createDate;
        private final LocalDateTime modifyDate;
        private final String author;
        private final Long voterCount;
        private final List<AnswerDto.AnswerDetailInfo> answerList;

        @Builder
        private DetailInfo(Integer id, String subject, String content, LocalDateTime createDate,
                LocalDateTime modifyDate, String author, Long voterCount,
                List<AnswerDetailInfo> answerList) {
            this.id = id;
            this.subject = subject;
            this.content = content;
            this.createDate = createDate;
            this.modifyDate = modifyDate;
            this.author = author;
            this.voterCount = voterCount;
            this.answerList = answerList;
        }
    }

    @Getter
    public static class MainInfo {
        private final Integer id;
        private final String subject;
        private final LocalDateTime createDate;
        private final Long answerCount;
        private final String author;

        @Builder
        private MainInfo(Integer id, String subject, LocalDateTime createDate, Long answerCount,
                String author) {
            this.id = id;
            this.subject = subject;
            this.createDate = createDate;
            this.answerCount = answerCount;
            this.author = author;
        }
    }
}
