package com.example.article_site.dto;

import com.example.article_site.domain.Answer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AnswerDetailDto {

    private Long id;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private String author;
    private List<CommentDto> commentList;
    public static AnswerDetailDto createAnswerDetailDto(Answer answer) {
        AnswerDetailDto dto = new AnswerDetailDto();
        dto.setAuthor(answer.getAuthor().getUsername());
        dto.setId(answer.getId());
        dto.setContent(answer.getContent());
        dto.setCreateDate(answer.getCreateDate());
        dto.setModifyDate(answer.getModifyDate());
        dto.setCommentList(
                answer.getCommentList().stream().map(CommentDto::createCommentDto).toList()
        );
        return dto;
    }
}
