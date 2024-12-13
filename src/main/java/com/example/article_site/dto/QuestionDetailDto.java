package com.example.article_site.dto;

import com.example.article_site.domain.Answer;
import com.example.article_site.domain.Question;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Comparator;

@Getter
@Setter
public class QuestionDetailDto {

    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private String author;
    private Page<AnswerDto> answerPage;
    private Integer answerCount;
    private Integer likes;
    private Long views;

    public static QuestionDetailDto createQuestionDetailDto(Question question, int pageNum, int size, Comparator<Answer> cmp) {
        QuestionDetailDto dto = new QuestionDetailDto();
        dto.setId(question.getId());
        dto.setSubject(question.getSubject());
        dto.setContent(question.getContent());
        dto.setCreateDate(question.getCreateDate());
        dto.setAuthor(question.getAuthor().getUsername());
        dto.setModifyDate(question.getModifyDate());
        Page<AnswerDto> page = AnswerDto.answerDtosPagingList(question.getAnswerList(), pageNum, size, cmp);
        dto.setAnswerPage(page);
        dto.setLikes(question.getVoter().size());
        dto.setAnswerCount(question.getAnswerList().size());
        dto.setViews(question.getViews());
        return dto;
    }
}
