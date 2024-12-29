package com.mysite.sbb.web.api.v1.user.dto.response;

import com.mysite.sbb.domain.comment.domain.Comment;
import com.mysite.sbb.web.api.v1.answer.dto.response.AnswerResponseDTO;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionListResponseDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String username;
    private String email;
    private List<QuestionListResponseDTO> questions;
    private List<AnswerResponseDTO> answers;
    private List<Comment> comments;
}
