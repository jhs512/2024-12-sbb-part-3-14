package baekgwa.sbb.domain.question.dto;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import baekgwa.sbb.domain.answer.dto.AnswerDto.AnswerDetailInfo;
import java.util.List;
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
        private final List<QuestionDto.QuestionCommentInfo> questionCommentList;

        @Builder
        private DetailInfo(Integer id, String subject, String content, LocalDateTime createDate,
                LocalDateTime modifyDate, String author, Long voterCount,
                Page<AnswerDetailInfo> answerList, boolean userVote,
                List<QuestionCommentInfo> questionCommentList) {
            this.id = id;
            this.subject = subject;
            this.content = content;
            this.createDate = createDate;
            this.modifyDate = modifyDate;
            this.author = author;
            this.voterCount = voterCount;
            this.answerList = answerList;
            this.userVote = userVote;
            this.questionCommentList = questionCommentList;
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

    @Getter
    public static class QuestionCommentInfo {
        private final Integer id;
        private final String content;
        private final String author;
        private final LocalDateTime createDate;

        @Builder
        private QuestionCommentInfo(Integer id, String content, String author,
                LocalDateTime createDate) {
            this.id = id;
            this.content = content;
            this.author = author;
            this.createDate = createDate;
        }
    }
}
