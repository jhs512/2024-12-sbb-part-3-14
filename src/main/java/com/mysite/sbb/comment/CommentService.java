package com.mysite.sbb.comment;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Specification<Comment> Voter(SiteUser siteUser) {
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true);
                return criteriaBuilder.isMember(siteUser, root.get("voter"));
            }
        };
    }

    public Comment getComment(int id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public Comment createAtComment(String content, Question question,SiteUser siteUser) {
        Comment c = new Comment();
        c.setContent(content);
        c.setQuestion(question);
        c.setAuthor(siteUser);
        c.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(c);
        return c;
    }

    public Comment createAtAnswer(String content, Answer answer, SiteUser siteUser) {
        Comment c = new Comment();
        c.setContent(content);
        c.setAnswer(answer);
        c.setAuthor(siteUser);
        c.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(c);
        return c;
    }

    public List<Comment> getList(Question question) {
        return this.commentRepository.findByQuestion(question);
    }
    public List<Comment> getList() {
        return this.commentRepository.findAll();
    }

    public void modify(Comment comment, String  content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }

    public List<Comment> getListByAuthor(int page, String username) {
        Pageable pageable = PageRequest.of(0, page);
        return this.commentRepository.findQuestionByAuthor(username, pageable);
    }


}
