package com.example.article_site.dto.profile;

import com.example.article_site.domain.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentProfileDto {
    private String content;
    private LocalDateTime createDate;

    public static CommentProfileDto createCommentProfileDto(Comment comment){
        CommentProfileDto dto = new CommentProfileDto();
        dto.setContent(comment.getContent());
        dto.setCreateDate(comment.getCreateDate());
        return dto;
    }
}
