package com.kkd.sbb.comment;

import com.kkd.sbb.DataNotFoundException;
import com.kkd.sbb.answer.Answer;
import com.kkd.sbb.question.Question;
import com.kkd.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment getComment(int id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isPresent()) {
            return comment.get();
        }else{
            throw new DataNotFoundException("comment not found");
        }
    }

    public Comment create(String content, Question question, Answer answer,
                          SiteUser siteUser) {
     Comment comment = new Comment();
     comment.setContent(content);
     comment.setQuestion(question);
     comment.setAnswer(answer);
     comment.setAuthor(siteUser);
     comment.setCreateDate(LocalDateTime.now());
     this.commentRepository.save(comment);
     return comment;
    }

    public List<Comment> getCommentList(Question question){
        return commentRepository.findByQuestion(question);
    }

    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }
}
