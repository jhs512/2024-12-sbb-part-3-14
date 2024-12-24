package baekgwa.sbb.domain.user.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

public class UserDto {

    @Getter
    public static class MypageInfo {
        private final String username;
        private final String userIntroduction;
        private final String email;
        private final Page<QuestionInfo> questionInfos;

        @Builder
        private MypageInfo(String username, String userIntroduction, String email,
                Page<QuestionInfo> questionInfos) {
            this.username = username;
            this.userIntroduction = userIntroduction;
            this.email = email;
            this.questionInfos = questionInfos;
        }
    }

    @Getter
    public static class QuestionInfo {
        private final Integer id;
        private final String subject;
        private final String content;
        private final LocalDateTime createDate;
        private final LocalDateTime modifyDate;

        @Builder
        private QuestionInfo(Integer id, String subject, String content, LocalDateTime createDate,
                LocalDateTime modifyDate) {
            this.id = id;
            this.subject = subject;
            this.content = content;
            this.createDate = createDate;
            this.modifyDate = modifyDate;
        }
    }
}
