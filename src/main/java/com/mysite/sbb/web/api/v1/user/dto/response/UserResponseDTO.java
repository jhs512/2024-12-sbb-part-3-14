package com.mysite.sbb.web.api.common.v1.user.dto.response;

import com.mysite.sbb.domain.comment.Comment;
import com.mysite.sbb.web.api.common.v1.answer.dto.response.AnswerResponseDTO;
import com.mysite.sbb.web.api.common.v1.question.dto.response.QuestionListResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String username;
    private String email;
    private List<QuestionListResponseDTO> questions;
    private List<AnswerResponseDTO> answers;
    private List<Comment> comments;
}
