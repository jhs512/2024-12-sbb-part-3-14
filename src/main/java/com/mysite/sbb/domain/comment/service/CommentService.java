package com.mysite.sbb.domain.comment.service;

import com.mysite.sbb.domain.comment.domain.Comment;
import com.mysite.sbb.domain.user.domain.SiteUser;
import com.mysite.sbb.web.api.v1.comment.dto.request.CommentRequestDTO;
import com.mysite.sbb.web.api.v1.comment.dto.response.CommentListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {

    Comment addComment(CommentRequestDTO dto, SiteUser author, Long questionId);

    List<Comment> getCommentsForQuestion(int questionId);

    List<Comment> getCommentsForAnswer(int answerId);

    Page<CommentListResponseDTO> getList(int page, String kw);
}
