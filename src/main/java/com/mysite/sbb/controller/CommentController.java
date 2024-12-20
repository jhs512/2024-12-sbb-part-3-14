package com.mysite.sbb.controller;

import com.mysite.sbb.model.question.entity.Question;
import com.mysite.sbb.model.user.entity.SiteUser;
import com.mysite.sbb.model.comment.dto.CommentRequestDTO;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import com.mysite.sbb.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {


    private QuestionServiceImpl questionService;
    private UserServiceImpl userService;

    // 질문에 댓글 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/{id}")
    public String createCommentForQuestion(Model model,
                                           @PathVariable Integer id,
                                           @Valid CommentRequestDTO commentRequestDTO,
                                           BindingResult bindingResult,
                                           Principal principal) {

        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("commentForm", commentRequestDTO); // 에러 메시지를 표시하기 위해 다시 전달
            return "question_detail";
        }

        // 댓글 생성 및 저장
        // commentService.createCommentForQuestion(question, commentForm.getContent(), siteUser);

        // 댓글 작성 후 리다이렉트
        return String.format("redirect:/question/detail/%s", id);

    }

}
