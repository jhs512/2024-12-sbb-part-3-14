package com.mysite.sbb.web.api.v1.question.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionRequestDTO {

    private Integer id;

    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "카테고리는 필수항목입니다.")
    private String category;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

}
