package com.site.sss.Comment.AnswerComment;

import com.site.sss.DataNotFoundException;
import com.site.sss.answer.Answer;
import com.site.sss.answer.AnswerService;
import com.site.sss.question.Question;
import com.site.sss.user.SiteUser;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerCommentService {

    private final AnswerCommentRepository answerCommentRepository;
    private final AnswerService answerService;

    public AnswerComment create(Answer answer, String content, SiteUser author) {
        AnswerComment answerComment = new AnswerComment();
        answerComment.setContent(content);
        answerComment.setCreateDate(LocalDateTime.now());
        answerComment.setAnswer(answer);
        answerComment.setAuthor(author);
        this.answerCommentRepository.save(answerComment);

        return answerComment;
    }

    public List<List<AnswerComment>> getList(Question question, int page) {
        List<List<AnswerComment>> allAnswerComment=new ArrayList<>();
        Page<Answer> answerPaging=answerService.getList(question,page);

        for (Answer answer : answerPaging.getContent()) {
            List<AnswerComment> answerCommentList=answerCommentRepository.findByAnswer(answer);
            allAnswerComment.add(answerCommentList);
        }

        return allAnswerComment;

    }

    public AnswerComment getAnswerComment(Integer id) {
        Optional<AnswerComment> answerComment = this.answerCommentRepository.findById(id);
        if (answerComment.isPresent()) {
            return answerComment.get();
        } else {
            throw new DataNotFoundException("questionComment not found");
        }
    }

    public void delete(AnswerComment answerComment) {
        this.answerCommentRepository.delete(answerComment);
    }
}
