package org.example.jtsb02.question.controller;

import static org.example.jtsb02.common.util.UserUtil.checkUserPermission;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.service.MemberService;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.form.QuestionForm;
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
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createQuestion(Model model, QuestionForm questionForm) {
        model.addAttribute("questionForm", questionForm);
        return "question/form/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult,
        Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question/form/create";
        }
        MemberDto member = memberService.getMember(principal.getName());
        Long questionId = questionService.createQuestion(questionForm, member);
        return String.format("redirect:/question/detail/%s", questionId);
    }

    @GetMapping("/list")
    public String getQuestions(@RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "kw", defaultValue = "") String kw, Model model) {

        Page<QuestionDto> questions = questionService.getQuestions(page, kw);
        model.addAttribute("paging", questions);
        model.addAttribute("kw", kw);
        return "question/list";
    }

    @GetMapping("/detail/{id}")
    public String getQuestion(@PathVariable("id") Long id, AnswerForm answerForm, Model model) {
        QuestionDto question = questionService.getQuestionWithHitsCount(id);
        model.addAttribute("question", question);
        model.addAttribute("answerForm", answerForm);
        return "question/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Long id, Model model,
        QuestionForm questionForm) {
        QuestionDto question = questionService.getQuestion(id);
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        model.addAttribute("questionForm", questionForm);
        return "question/form/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Long id, @Valid QuestionForm questionForm,
        BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question/form/modify";
        }
        QuestionDto question = questionService.getQuestion(id);
        checkUserPermission(principal.getName(), question.getAuthor().getMemberId(), "수정");
        questionService.modifyQuestion(id, questionForm);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Long id, Principal principal) {
        QuestionDto question = questionService.getQuestion(id);
        checkUserPermission(principal.getName(), question.getAuthor().getMemberId(), "삭제");
        questionService.deleteQuestion(id);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteQuestion(@PathVariable("id") Long id, Principal principal) {
        MemberDto member = memberService.getMember(principal.getName());
        questionService.voteQuestion(id, member);
        return String.format("redirect:/question/detail/%s", id);
    }
}
