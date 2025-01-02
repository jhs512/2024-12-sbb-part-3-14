package org.example.jtsb02.comment.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.comment.entity.Comment;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.question.dto.QuestionDto;

@Getter
@Builder
public class CommentDto {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private QuestionDto question;
    private AnswerDto answer;
    private MemberDto author;
    private Set<MemberDto> voter;

    public static CommentDto QuestionCommentDtoFromComment(Comment comment) {
        return CommentDto.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .question(QuestionDto.fromQuestion(comment.getQuestion()))
            .author(MemberDto.fromMember(comment.getAuthor()))
            .voter(comment.getVoter().stream().map(MemberDto::fromMember).collect(Collectors.toSet()))
            .build();
    }

    public static CommentDto AnswerCommentDtoFromComment(Comment comment) {
        return CommentDto.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .question(QuestionDto.fromQuestion(comment.getAnswer().getQuestion()))
            .answer(AnswerDto.fromAnswer(comment.getAnswer()))
            .author(MemberDto.fromMember(comment.getAuthor()))
            .voter(comment.getVoter().stream().map(MemberDto::fromMember).collect(Collectors.toSet()))
            .build();
    }
}
