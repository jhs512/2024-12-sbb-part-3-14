package org.example.jtsb02.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.member.form.MemberForm;
import org.example.jtsb02.member.service.MemberService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup(MemberForm memberForm, Model model) {
        model.addAttribute("memberForm", memberForm);
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberForm memberForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberForm", memberForm);
            return "member/signup";
        }

        if(!memberForm.getPassword().equals(memberForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "passwordInCorrect",
                "패스워드 확인이 일치하지 않습니다.");
            model.addAttribute("memberForm", memberForm);
            return "member/signup";
        }

        try {
            memberService.createMember(memberForm);
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "member/signup";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "member/signup";
        }

        return "redirect:/";
    }
}