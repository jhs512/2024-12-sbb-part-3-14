package com.ll.pratice1.domain.comment.controller;

import com.ll.pratice1.domain.comment.CommentForm;
import com.ll.pratice1.domain.comment.service.CommentService;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.question.service.QuestionService;
import com.ll.pratice1.domain.user.SiteUser;
import com.ll.pratice1.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final QuestionService questionService;
    private final UserService userService;
    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/{id}")
    public String commentCreate(Model model, Principal principal, @PathVariable("id") Integer id,
                                @Valid CommentForm commentForm, BindingResult bindingResult){
        Question question = this.questionService.getQuestion(id);
        if(bindingResult.hasErrors()){
            model.addAttribute("question", question);
            return "question_detail";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.create(question, commentForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s", question.getId());
    }

}
