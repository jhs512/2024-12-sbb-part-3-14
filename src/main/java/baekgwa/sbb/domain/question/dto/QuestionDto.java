package baekgwa.sbb.domain.question.dto;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
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
        private final Page<AnswerDto.AnswerDetailInfo> answerList;
        private final boolean userVote;

        @Builder
        private DetailInfo(Integer id, String subject, String content, LocalDateTime createDate,
                LocalDateTime modifyDate, String author, Long voterCount,
                Page<AnswerDto.AnswerDetailInfo> answerList, boolean userVote) {
            this.id = id;
            this.subject = subject;
            this.content = content;
            this.createDate = createDate;
            this.modifyDate = modifyDate;
            this.author = author;
            this.voterCount = voterCount;
            this.answerList = answerList;
            this.userVote = userVote;
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
