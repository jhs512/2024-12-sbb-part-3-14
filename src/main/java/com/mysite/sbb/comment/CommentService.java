package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void create(Answer answer, String content, SiteUser author) {
        Comment comment = Comment.builder()
                .author(author)
                .content(content)
                .answer(answer)
                .build();
        this.commentRepository.save(comment);
    }
}
