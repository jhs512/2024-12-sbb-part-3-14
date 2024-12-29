package com.mysite.sbb.web.api.common.v1.question.dto.response;

import com.mysite.sbb.domain.comment.Comment;
import com.mysite.sbb.web.api.common.v1.answer.dto.response.AnswerResponseDTO;
import com.mysite.sbb.domain.answer.Answer;
import com.mysite.sbb.domain.question.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDetailResponseDTO {
    private long id;                         // ID
    private String subject;                 // 제목
    private String content;                 // 내용
    private String authorName;              // 작성자
    private String category;
    private LocalDateTime createDate;       // 생성일
    private LocalDateTime modifyDate;       // 수정일
    private int answerCount;                // 답변 개수
    private int voterCount;                         // 추천 수
    private Page<AnswerResponseDTO> answers;        // 답변 리스트
    private Map<Long, List<Comment>> commentsForAnswers;

    public QuestionDetailResponseDTO(Question question, Page<Answer>  answers, Map<Integer, List<Comment>> commentsForAnswers) {
        this.id = question.getId();
        this.subject = question.getSubject();
        this.content = question.getContent();
        this.authorName = question.getAuthor() != null ? question.getAuthor().getUsername() : "익명";
        this.category = question.getCategory() != null ? question.getCategory().getName() : "없음";
        this.createDate = question.getCreateDate();
        this.modifyDate = question.getModifyDate();
        this.answerCount = question.getAnswerList().size();
        this.voterCount = question.getVoter().size();   // 추천 개수
        this.answers = answers
                .map(answer -> new AnswerResponseDTO(answer, commentsForAnswers.get(answer.getId())));

    }
}
