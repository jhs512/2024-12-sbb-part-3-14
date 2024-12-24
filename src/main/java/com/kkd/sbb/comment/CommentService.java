package com.kkd.sbb.comment;

import com.kkd.sbb.DataNotFoundException;
import com.kkd.sbb.answer.Answer;
import com.kkd.sbb.question.Question;
import com.kkd.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Page<Comment> getListByAuthor(int page, SiteUser user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return this.commentRepository.findByAuthor(user, pageable);
    }
}
