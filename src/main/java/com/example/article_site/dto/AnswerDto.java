package com.example.article_site.dto;

import com.example.article_site.domain.Answer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AnswerDto {

    private Long id;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private String author;
    private Integer likes;

    public static AnswerDto createAnswerDto(Answer answer) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setId(answer.getId());
        answerDto.setContent(answer.getContent());
        answerDto.setCreateDate(answer.getCreateDate());
        answerDto.setModifyDate(answer.getModifyDate());
        answerDto.setAuthor(answer.getAuthor().getUsername());
        answerDto.setLikes(answer.getVoter().size());
        return answerDto;
    }

    public static Page<AnswerDto> answerDtosPagingList(List<Answer> answers, int page, int size, Comparator<Answer> cmp) {
        List<Answer> sortedAnswers = answers.stream()
                .sorted(cmp)
                .toList();

        int start = page * size;
        int end = Math.min(start + size, sortedAnswers.size());

        List<AnswerDto> pageContent = sortedAnswers.subList(start, end)
                .stream()
                .map(AnswerDto::createAnswerDto)
                .collect(Collectors.toList());

        return new PageImpl<>(pageContent, PageRequest.of(page, size), sortedAnswers.size());
    }
}

