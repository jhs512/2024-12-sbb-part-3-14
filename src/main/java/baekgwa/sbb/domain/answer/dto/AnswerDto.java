package baekgwa.sbb.domain.answer.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

public class AnswerDto {

    @Getter
    public static class AnswerInfo {
        private String username;
        private String content;

        @Builder
        private AnswerInfo(String username, String content) {
            this.username = username;
            this.content = content;
        }
    }

    @Getter
    public static class AnswerDetailInfo {
        private final Integer id;
        private final String content;
        private final LocalDateTime modifyDate;
        private final LocalDateTime createDate;
        private final String author;
        private final Long voteCount;
        private final boolean userVote;

        @Builder
        private AnswerDetailInfo(Integer id, String content, LocalDateTime modifyDate,
                LocalDateTime createDate, String author, Long voteCount, boolean userVote) {
            this.id = id;
            this.content = content;
            this.modifyDate = modifyDate;
            this.createDate = createDate;
            this.author = author;
            this.voteCount = voteCount;
            this.userVote = userVote;
        }
    }
}
