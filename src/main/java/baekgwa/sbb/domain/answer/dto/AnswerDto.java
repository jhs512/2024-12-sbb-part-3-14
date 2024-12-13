package baekgwa.sbb.domain.answer.dto;

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

}
