package com.mysite.sbb.Comment;

import com.mysite.sbb.Answer.Answer;
import com.mysite.sbb.Question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    // 댓글 생성
    public void create(Question question, Answer answer, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setQuestion(question);
        comment.setAnswer(answer);
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    // 최근 댓글 가져오기
    public List<Comment> getRecentComments(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createDate"));
        return commentRepository.findAll(pageable).getContent();
    }


}