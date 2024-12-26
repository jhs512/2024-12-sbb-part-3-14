package com.mysite.sbb.domain.comment;


import com.mysite.sbb.web.comment.dto.request.CommentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl {

    private CommentRepository commentRepository;

    @Transactional
    public void createComment(CommentRequestDTO dto, String authorName) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setAuthor(authorName);


        commentRepository.save(comment);
    }
}
