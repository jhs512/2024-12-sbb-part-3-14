package org.example.jtsb02.answer.controller;

import static org.example.jtsb02.common.util.UserUtil.checkUserPermission;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.service.AnswerService;
import org.example.jtsb02.comment.form.CommentForm;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.service.MemberService;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(@PathVariable("id") Long questionId, @Valid AnswerForm answerForm,
        BindingResult bindingResult, Model model, Principal principal) {
        QuestionDto question = questionService.getQuestionWithHitsCount(questionId, 1, "");
        MemberDto member = memberService.getMemberByMemberId(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("answerForm", answerForm);
            model.addAttribute("commentForm", new CommentForm());
            return "question/detail";
        }
        Long answerId = answerService.createAnswer(questionId, answerForm, member);
        return String.format("redirect:/question/detail/%s?page=%d#answer_%s", questionId,
            question.getAnswerCount() / 10 + 1, answerId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyAnswer(@PathVariable("id") Long answerId, AnswerForm answerForm,
        Model model) {
        AnswerDto answer = answerService.getAnswer(answerId);
        answerForm.setContent(answer.getContent());
        model.addAttribute("answerForm", answerForm);
        return "answer/form/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyAnswer(@PathVariable("id") Long answerId, @Valid AnswerForm answerForm,
        BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer/form/modify";
        }
        AnswerDto answer = answerService.getAnswer(answerId);
        checkUserPermission(principal.getName(), answer.getAuthor().getMemberId(), "수정");
        answerService.modifyAnswer(answerId, answerForm);
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answerId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteAnswer(@PathVariable("id") Long answerId, Principal principal) {
        AnswerDto answer = answerService.getAnswer(answerId);
        checkUserPermission(principal.getName(), answer.getAuthor().getMemberId(), "삭제");
        answerService.deleteAnswer(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteAnswer(@PathVariable("id") Long answerId, Principal principal) {
        AnswerDto answer = answerService.getAnswer(answerId);
        MemberDto member = memberService.getMemberByMemberId(principal.getName());
        answerService.voteAnswer(answerId, member);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @GetMapping("/list")
    public String getAnswers(@RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "kw", defaultValue = "") String kw, Model model) {
        Page<AnswerDto> answers = answerService.getAnswers(page);
        model.addAttribute("paging", answers);
        model.addAttribute("kw", kw);
        return "answer/list";
    }
}