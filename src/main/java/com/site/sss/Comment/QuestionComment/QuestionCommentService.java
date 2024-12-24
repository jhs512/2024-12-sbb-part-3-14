package com.site.sss.Comment.QuestionComment;

import com.site.sss.DataNotFoundException;
import com.site.sss.question.Question;
import com.site.sss.user.SiteUser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionCommentService {


    private final QuestionCommentRepository questionCommentRepository;

    public QuestionComment create(Question question, String content, SiteUser author) {
        QuestionComment questionComment = new QuestionComment();
        questionComment.setContent(content);
        questionComment.setCreateDate(LocalDateTime.now());
        questionComment.setQuestion(question);
        questionComment.setAuthor(author);
        this.questionCommentRepository.save(questionComment);

        return questionComment;
    }

    public List<QuestionComment> getList(Question question) {
        List<QuestionComment> QuestionCommentList=questionCommentRepository.findByQuestion(question);

        return QuestionCommentList;
    }

    public  QuestionComment getQuestionComment(Integer id) {
        Optional<QuestionComment> questionComment = this.questionCommentRepository.findById(id);
        if (questionComment.isPresent()) {
            return questionComment.get();
        } else {
            throw new DataNotFoundException("questionComment not found");
        }
    }

    public void delete(QuestionComment questionComment) {
        this.questionCommentRepository.delete(questionComment);
    }
}
