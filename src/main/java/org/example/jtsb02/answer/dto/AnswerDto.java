package org.example.jtsb02.answer.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.question.dto.QuestionDto;

@Getter
@Builder
public class AnswerDto {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private QuestionDto question;
    private List<CommentDto> comments;
    private MemberDto author;
    private Set<MemberDto> voter;

    public static AnswerDto fromAnswer(Answer answer) {
        return AnswerDto.builder()
            .id(answer.getId())
            .content(answer.getContent())
            .createdAt(answer.getCreatedAt())
            .modifiedAt(answer.getModifiedAt())
            .question(QuestionDto.OnlyIdFromQuestion(answer.getQuestion()))
            .comments(answer.getComments().stream().map(CommentDto::AnswerCommentDtoFromComment).toList())
            .author(MemberDto.fromMember(answer.getAuthor()))
            .voter(answer.getVoter().stream().map(MemberDto::fromMember).collect(Collectors.toSet()))
            .build();
    }
}
