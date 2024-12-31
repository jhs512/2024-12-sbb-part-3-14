package com.mysite.sbb.password.controller;

import com.mysite.sbb.password.form.ChangePasswordForm;
import com.mysite.sbb.password.service.EmailService;
import com.mysite.sbb.password.service.PasswordService;
import com.mysite.sbb.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/password")
@RequiredArgsConstructor
@Controller
public class PasswordController {

    private final PasswordService passwordService;
    private final UserService userService;

    @PostMapping("/find")
    public String findPassword(@RequestParam String email, Model model) {
        try {
            boolean success = passwordService.sendTemporaryPassword(email);
            if (success) {
                model.addAttribute("message", "임시 비밀번호가 등록된 이메일로 발송 되었습니다. ");
            } else {
                model.addAttribute("error","등록된 메일이 없습니다. ");
            }
        } catch (Exception e) {
            model.addAttribute("error", "비밀번호 찾기 중 오류 발생");
        }

        return "login_form";
    }

    @GetMapping("/change")
    public String changePassword(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());

        return "change_password";
    }

    @PostMapping("/change")
    public String changePassword(@Validated ChangePasswordForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "change_password";
        }

        try {
            passwordService.updatePassword(form.getCurrentPassword(),form.getNewPassword());
            model.addAttribute("message","비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException  ie) {
            System.out.println("IllegalArgumentException 발생: " + ie.getMessage());
            result.rejectValue("currentPassword", "password.incorrect", ie.getMessage());

            return "change_password";
        } catch (Exception e) {
            System.out.println("Exception 발생: " + e.getMessage());
            model.addAttribute("error", "비밀번호 변경 중 오류가 발생했습니다.");

            return "change_password";
        }

        return "redirect:/";
    }
}
