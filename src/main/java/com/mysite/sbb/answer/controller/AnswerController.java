package com.mysite.sbb.answer.controller;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.entity.AnswerForm;
import com.mysite.sbb.answer.service.AnswerService;
import com.mysite.sbb.global.util.Check;
import com.mysite.sbb.global.util.HttpMethod;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.service.QuestionService;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final AnswerService answerService;
    private final QuestionService questionService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{questionId}")
    public String createAnswer(Model model, @PathVariable("questionId") Integer questionId,
                               @Valid AnswerForm answerForm, BindingResult bindingResult,
                               Principal principal) {
        Question question = this.questionService.findQuestionById(questionId);
        SiteUser siteUser = this.userService.findUser(principal.getName());
        if(bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Answer answer = this.answerService.createAnswer(question, answerForm.getContent(), siteUser);
        return "redirect:/question/detail/%s#answer_%s".formatted(questionId, answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyAnswer(@PathVariable("id") Integer answerId, Principal principal, AnswerForm answerForm) {
        Answer answer = this.answerService.findAnswer(answerId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(answer, principal.getName(), HttpMethod.MODIFY);

        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyAnswer(@PathVariable("id") Integer answerId, Principal principal,
                               @Valid AnswerForm answerForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = this.answerService.findAnswer(answerId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(answer, principal.getName(), HttpMethod.MODIFY);

        this.answerService.modifyAnswer(answer, answerForm.getContent());
        return "redirect:/question/detail/%s#answer_%s".formatted(answer.getQuestion().getId(), answerId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteAnswer(@PathVariable("id") Integer answerId, Principal principal) {
        Answer answer = this.answerService.findAnswer(answerId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(answer, principal.getName(), HttpMethod.DELETE);

        this.answerService.delete(answer);
        return "redirect:/question/detail/%s".formatted(answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteAnswer(@PathVariable("id") Integer answerId, Principal principal) {
        Answer answer = this.answerService.findAnswer(answerId);
        SiteUser voteUser = this.userService.findUser(principal.getName());
        this.answerService.voteAnswer(answer, voteUser);
        return "redirect:/question/detail/%s#answer_%s".formatted(answer.getQuestion().getId(), answerId);
    }
}
