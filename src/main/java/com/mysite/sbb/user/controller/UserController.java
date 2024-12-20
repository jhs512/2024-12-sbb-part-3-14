package com.mysite.sbb.user.controller;

import com.mysite.sbb.user.form.UserCreateForm;
import com.mysite.sbb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 화면 form 받아오는 기능
    @GetMapping("/signup")
    public String signUp(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    // 입력값 처리 기능
    @PostMapping("/signup")
    public String signUp(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword().equals(userCreateForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.nomatch",
                    "2개의 PW가 일치하지 않습니다. ");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                               userCreateForm.getEmail(),
                               userCreateForm.getPassword());
        } catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
}
