package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.InputMismatchException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
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

    @GetMapping("/login/temp_password")
    public String tempPassword() {
        return "email_check_form";
    }

    @PostMapping("/login/temp_password")
    public String tempPassword(String email, Model model) {
        try {
            SiteUser user = userService.getUserByEmail(email);
            userService.sendTemporaryPassword(user);
        } catch (DataNotFoundException e) {
            model.addAttribute("error", "등록되지 않은 사용자입니다.");
            return "email_check_form";
        }

        return "redirect:/user/login";
    }

    @GetMapping("/password_change")
    public String passwordChange() {
        return "password_change_form";
    }

    @PostMapping("/password_change")
    public String passwordChange(
            @RequestParam("old_password") String oldPassword,
            @RequestParam("new_password")String newPassword,
            Model model,
            Principal principal) {
        try {
            SiteUser user = userService.getUser(principal.getName());
            userService.changePassword(user, oldPassword, newPassword);
        } catch (InputMismatchException e) {
            model.addAttribute("error", "현재 비밀번호를 다시 입력해주세요.");
            return "password_change_form";
        }

        return "redirect:/";
    }
}
