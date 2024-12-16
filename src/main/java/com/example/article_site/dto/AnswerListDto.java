package com.example.article_site.dto;

import com.example.article_site.domain.Answer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnswerListDto {

    private Long id;
    private String questionTitle;
    private String content;
    private LocalDateTime createdDate;
    private String author;
    private Integer commentCount;

    private static final int MAX_CONTENT_SIZE = 15;
    public static AnswerListDto createAnswerListDto(Answer answer) {
        AnswerListDto dto = new AnswerListDto();
        dto.setId(answer.getId());
        dto.setQuestionTitle(answer.getQuestion().getSubject());
        dto.setCreatedDate(answer.getCreateDate());
        dto.setAuthor(answer.getAuthor().getUsername());
        dto.setCommentCount(answer.getCommentList().size());

        // 보기 좋게 하기 위해서 너무 길면 content 후반부를 ... 로 생략한다.
        String content = answer.getContent();
        if(content.length() > MAX_CONTENT_SIZE){
            dto.setContent(content.substring(0, MAX_CONTENT_SIZE) + "...");
        }else{
            dto.setContent(content);
        }

        return dto;
    }
}
