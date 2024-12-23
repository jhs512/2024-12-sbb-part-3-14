package com.mysite.sbb.controller;

import com.mysite.sbb.domain.comment.dto.CommentRequestDTO;
import com.mysite.sbb.service.impl.CommentServiceImpl;
import com.mysite.sbb.service.QuestionService;
import com.mysite.sbb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private CommentServiceImpl commentServiceImpl;
    private QuestionService questionService;
    private UserService userService;

    // 질문에 댓글 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createCommentForQuestion(Model model,
                                           @Valid CommentRequestDTO commentRequestDTO,
                                           BindingResult bindingResult,
                                           Principal principal) {

        if (bindingResult.hasErrors()) {
            // 에러 발생 시 question_detail 또는 answer_detail 로 리턴
            // targetType에 따라 다른 뷰로 리다이렉트 가능
            return "redirect:/question/detail/" + commentRequestDTO.getTargetId();
        }

        // 댓글 생성 및 저장
        // commentServiceImpl.createComment(question, commentRequestDTO.getContent(), principal.getName());
        commentServiceImpl.createComment(commentRequestDTO, principal.getName());

        // 댓글 작성 후 리다이렉트
        return String.format("redirect:/question/detail/%s", commentRequestDTO.getTargetId());
    }

}
