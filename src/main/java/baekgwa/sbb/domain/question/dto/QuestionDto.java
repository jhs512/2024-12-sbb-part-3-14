package baekgwa.sbb.domain.question.dto;

import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.user.entity.SiteUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

public class QuestionDto {

    @Getter
    public static class DetailInfo {
        private final Integer id;
        private final String subject;
        private final String content;
        private final Set<Answer> answerList;
        private final LocalDateTime createDate;
        private final LocalDateTime modifyDate;
        private final SiteUser author;
        private final Set<SiteUser> voter;

        @Builder
        private DetailInfo(Integer id, String subject, String content, Set<Answer> answerList,
                LocalDateTime createDate, LocalDateTime modifyDate, SiteUser author,
                Set<SiteUser> voter) {
            this.id = id;
            this.subject = subject;
            this.content = content;
            this.answerList = answerList;
            this.createDate = createDate;
            this.modifyDate = modifyDate;
            this.author = author;
            this.voter = voter;
        }
    }

    @Getter
    public static class MainInfo {
        private final Integer id;
        private final String subject;
        private final LocalDateTime createDate;
        private final Set<Answer> answerList;
        private final SiteUser author;

        @Builder
        private MainInfo(Integer id, String subject, LocalDateTime createDate,
                Set<Answer> answerList,
                SiteUser author) {
            this.id = id;
            this.subject = subject;
            this.createDate = createDate;
            this.answerList = answerList;
            this.author = author;
        }
    }
}
