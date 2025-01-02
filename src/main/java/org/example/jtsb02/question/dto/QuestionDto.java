package org.example.jtsb02.question.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.question.entity.Question;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class QuestionDto {

    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int hits;
    private Page<AnswerDto> answers;
    private List<CommentDto> comments;
    private int answerCount;
    private MemberDto author;
    private Set<MemberDto> voter;

    public static QuestionDto fromQuestion(Question question) {
        return QuestionDto.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .content(question.getContent())
            .createdAt(question.getCreatedAt())
            .hits(question.getHits())
            .answerCount(question.getAnswers().size())
            .author(MemberDto.fromMember(question.getAuthor()))
            .voter(question.getVoter().stream().map(MemberDto::fromMember).collect(Collectors.toSet()))
            .build();
    }

    public static QuestionDto fromQuestion(Question question, Page<AnswerDto> answerPage) {
        return QuestionDto.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .content(question.getContent())
            .createdAt(question.getCreatedAt())
            .modifiedAt(question.getModifiedAt())
            .hits(question.getHits())
            .answers(answerPage)
            .comments(question.getComments().stream().map(CommentDto::QuestionCommentDtoFromComment).toList())
            .author(MemberDto.fromMember(question.getAuthor()))
            .voter(question.getVoter().stream().map(MemberDto::fromMember).collect(Collectors.toSet()))
            .build();
    }

    public static QuestionDto OnlyIdFromQuestion(Question question) {
        return QuestionDto.builder().id(question.getId()).build();
    }
}