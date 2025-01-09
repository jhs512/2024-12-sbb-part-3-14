package com.mysite.sbb.comment.controller;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.entity.form.AnswerForm;
import com.mysite.sbb.answer.service.AnswerService;
import com.mysite.sbb.comment.entity.Commentable;
import com.mysite.sbb.comment.entity.form.CommentForm;
import com.mysite.sbb.comment.service.CommentService;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.service.QuestionService;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.AnnotatedWildcardType;
import java.security.Principal;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final QuestionService questionService;
    private final CommentService commentService;
    private final UserService userService;
    private final AnswerService answerService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String create(@PathVariable long id,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         Principal principal,
                         @Valid CommentForm commentForm,
                         BindingResult bindingResult,
                         Model model,
                         AnswerForm answerForm,
                         @RequestParam(value = "kind") String kind,
                         @RequestParam(value = "answerId", defaultValue = "0") long answerId) {

        Question question = questionService.findById(id);
        Page<Answer> paging = answerService.findByQuestionId(id, page);

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("paging", paging);
            return "question/question_detail";
        }

        SiteUser siteUser = userService.findByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if ("answer".equals(kind)) {
            Commentable answer = answerService.findById(answerId);
            commentService.write(answer, siteUser, commentForm.getContent());
        }
        else commentService.write(question, siteUser, commentForm.getContent());

        return "redirect:/question/detail/%s".formatted(id);
    }

}
