package com.mysite.sbb.comment;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment create(Answer answer, String content, SiteUser author) {
        Comment comment = Comment.builder()
                .author(author)
                .content(content)
                .answer(answer)
                .build();
        return this.commentRepository.save(comment);
    }

    public Comment getComment(int id){
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if(optionalComment.isEmpty()) {
            throw new DataNotFoundException("answer not found");
        }
        return optionalComment.get();
    }

    public void modify(Comment comment, String content){
        comment.setContent(content);
        this.commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public List<Comment> getMyCommentList(String username) {
        return this.commentRepository.findAllByAuthor_Username(username);
    }

    public void vote(SiteUser user, Comment comment) {
        comment.getVoter().add(user);
        this.commentRepository.save(comment);
    }
}
