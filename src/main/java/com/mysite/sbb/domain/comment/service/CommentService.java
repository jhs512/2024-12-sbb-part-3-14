package com.mysite.sbb.domain.comment;

import com.mysite.sbb.domain.user.SiteUser;
import com.mysite.sbb.web.api.v1.comment.dto.request.CommentRequestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {
    @Transactional
    Comment addComment(CommentRequestDTO dto, SiteUser author, Long questionId);

    @Transactional(readOnly = true)
    List<Comment> getCommentsForQuestion(int questionId);

    @Transactional(readOnly = true)
    List<Comment> getCommentsForAnswer(int answerId);
}
