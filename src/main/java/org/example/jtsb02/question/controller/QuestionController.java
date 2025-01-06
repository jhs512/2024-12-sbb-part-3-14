package org.example.jtsb02.question.controller;

import static org.example.jtsb02.common.util.UserUtil.checkUserPermission;
import static org.example.jtsb02.common.util.UserUtil.getUsernameFromPrincipal;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.category.dto.CategoryDto;
import org.example.jtsb02.category.service.CategoryService;
import org.example.jtsb02.comment.form.CommentForm;
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
    private final CategoryService categoryService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createQuestion(Model model, QuestionForm questionForm) {
        List<CategoryDto> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("questionForm", questionForm);
        return "question/form/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult,
        Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            List<CategoryDto> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("questionForm", questionForm);
            return "question/form/create";
        }
        String memberId = getUsernameFromPrincipal(principal);
        MemberDto member = memberService.getMemberByMemberId(memberId);
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
    public String getQuestion(@PathVariable("id") Long id,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "sort", defaultValue = "") String sort,
        AnswerForm answerForm, CommentForm commentForm, Model model) {
        QuestionDto question = questionService.getQuestionWithHitsCount(id, page, sort);
        model.addAttribute("question", question);
        model.addAttribute("answerForm", answerForm);
        model.addAttribute("commentForm", commentForm);
        return "question/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Long id, Model model,
        QuestionForm questionForm) {
        List<CategoryDto> categories = categoryService.getAllCategories();
        QuestionDto question = questionService.getQuestion(id);
        questionForm.setCategoryId(question.getCategory().getId());
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        model.addAttribute("questionForm", questionForm);
        model.addAttribute("categories", categories);
        return "question/form/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Long id, @Valid QuestionForm questionForm,
        BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            List<CategoryDto> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("questionForm", questionForm);
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
        MemberDto member = memberService.getMemberByMemberId(principal.getName());
        questionService.voteQuestion(id, member);
        return String.format("redirect:/question/detail/%s", id);
    }
}
