package com.mysite.sbb.answer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerForm {
    @NotEmpty(message="내용은 필수항목입니다.")
    private String markdownEditorAnswerNew;
}
