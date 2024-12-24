package com.mysite.sbb.comment.controller;

import com.mysite.sbb.answer.service.AnswerService;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.comment.entity.CommentForm;
import com.mysite.sbb.comment.service.CommentService;
import com.mysite.sbb.global.util.Check;
import com.mysite.sbb.global.util.HttpMethod;
import com.mysite.sbb.question.service.QuestionService;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final QuestionService questionService;
    private final CommentService commentService;
    private final UserService userService;
    private final AnswerService answerService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createComment(@RequestParam("questionId") Integer questionId,
                                @RequestParam(value = "parentId", required = false, defaultValue = "0") Integer parentId,
                                Model model, @ModelAttribute CommentForm commentForm) {
        String parentContent;
        if(parentId.equals(0)){
            parentContent = this.questionService.findQuestionById(questionId).getContent();
        } else {
            parentContent = this.answerService.findAnswer(parentId).getContent();
        }
        commentForm.setQuestionId(questionId);
        commentForm.setParentId(parentId);
        commentForm.setParentContent(parentContent);

        model.addAttribute("commentForm", commentForm);
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createComment(@Valid CommentForm commentForm, BindingResult bindingResult, Model model,
                                Principal principal) {
        if(bindingResult.hasErrors()){
            model.addAttribute("commentForm", commentForm);
            return "comment_form";
        }
        SiteUser siteUser = this.userService.findUser(principal.getName());

        this.commentService.createComment(commentForm.getQuestionId(), commentForm.getContent(),
                commentForm.getParentId(), siteUser);

        return "redirect:/question/detail/%s".formatted(commentForm.getQuestionId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{commentId}")
    public String modifyComment(@PathVariable("commentId") Integer commentId, Principal principal,
                                CommentForm commentForm) {
        Comment comment = this.commentService.findComment(commentId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(comment, principal.getName(), HttpMethod.MODIFY);

        commentForm.setContent(comment.getContent());
        commentForm.setQuestionId(comment.getQuestionId());
        commentForm.setParentId(comment.getParentId());

        String parentContent;
        if(comment.getParentId().equals(0)){
            parentContent = this.questionService.findQuestionById(comment.getQuestionId()).getContent();
        } else {
            parentContent = this.answerService.findAnswer(comment.getParentId()).getContent();
        }
        commentForm.setParentContent(parentContent);

        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{commentId}")
    public String modifyComment(@PathVariable("commentId") Integer commentId, Principal principal,
                                @Valid CommentForm commentForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment comment = this.commentService.findComment(commentId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(comment, principal.getName(), HttpMethod.MODIFY);

        this.commentService.modifyComment(comment, commentForm.getContent());
        return "redirect:/question/detail/%s".formatted(commentForm.getQuestionId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable("commentId") Integer commentId, Principal principal) {
        Comment comment = this.commentService.findComment(commentId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(comment, principal.getName(), HttpMethod.DELETE);

        this.commentService.deleteComment(comment);
        return "redirect:/question/detail/%s".formatted(comment.getQuestionId());
    }
}
