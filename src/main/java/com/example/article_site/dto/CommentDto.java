package com.example.article_site.dto;

import com.example.article_site.domain.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private String author;
    private String content;
    private LocalDateTime createDate;

    public static CommentDto createCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setContent(comment.getContent());
        dto.setCreateDate(comment.getCreateDate());
        dto.setAuthor(comment.getAuthor().getUsername());
        return dto;
    }
}
