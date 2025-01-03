package org.example.jtsb02.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.service.AnswerService;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.comment.service.CommentService;
import org.example.jtsb02.common.exception.PasswordNotMatchException;
import org.example.jtsb02.mail.form.MailForm;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.form.MemberForm;
import org.example.jtsb02.member.form.PasswordUpdateForm;
import org.example.jtsb02.member.service.MemberService;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.service.QuestionService;
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping("/member/")
public class MemberController {

    private final MemberService memberService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    @GetMapping("/signup")
    public String signup(MemberForm memberForm, Model model) {
        model.addAttribute("memberForm", memberForm);
        return "member/form/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberForm memberForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberForm", memberForm);
            return "member/form/signup";
        }

        if (!memberForm.getPassword().equals(memberForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "passwordInCorrect",
                "패스워드 확인이 일치하지 않습니다.");
            model.addAttribute("memberForm", memberForm);
            return "member/form/signup";
        }

        try {
            memberService.createMember(memberForm);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "member/form/signup";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "member/form/signup";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "member/form/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{id}")
    public String memberDetail(@PathVariable("id") Long id,
        @RequestParam(value = "questionPage", defaultValue = "1") int questionPage,
        @RequestParam(value = "answerPage", defaultValue = "1") int answerPage,
        @RequestParam(value = "commentPage", defaultValue = "1") int commentPage, Model model) {

        MemberDto member = memberService.getMemberById(id);
        Page<QuestionDto> question = questionService.getQuestionsByAuthorId(id, questionPage);
        Page<AnswerDto> answer = answerService.getAnswersByAuthorId(id, answerPage);
        Page<CommentDto> comment = commentService.getCommentsByAuthorId(id, commentPage);

        model.addAttribute("member", member);
        model.addAttribute("questionPaging", question);
        model.addAttribute("answerPaging", answer);
        model.addAttribute("commentPaging", comment);
        return "member/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update/password/{id}")
    public String updatePassword(@PathVariable("id") Long id, PasswordUpdateForm passwordUpdateForm,
        Model model) {
        model.addAttribute("passwordUpdateForm", passwordUpdateForm);
        return "member/form/password";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/password/{id}")
    public String updatePassword(@PathVariable("id") Long id, @Valid PasswordUpdateForm passwordUpdateForm,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/form/password";
        }

        try {
            memberService.verifyPassword(id, passwordUpdateForm);
        } catch (PasswordNotMatchException e) {
            bindingResult.rejectValue("oldPassword", "passwordNotMatch", "기존 비밀번호가 일치하지 않습니다.");
            return "member/form/password";
        }
        if(!passwordUpdateForm.getNewPassword().equals(passwordUpdateForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "passwordInCorrect"
                , "2개의 패스워드가 일치하지 않습니다.");
            return "member/form/password";
        }

        memberService.updatePassword(id, passwordUpdateForm);
        return "redirect:/member/logout";
    }

    @GetMapping("/find/password")
    public String findPassword(Model model, MailForm mailForm) {
        model.addAttribute("mailForm", mailForm);
        return "mail/form";
    }
}