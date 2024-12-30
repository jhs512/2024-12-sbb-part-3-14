package com.mysite.sbb.user.controller;

import com.mysite.sbb.user.entity.*;
import com.mysite.sbb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "signup_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2", "passwordIncorrect",
                    "비밀번호가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.createUser(userCreateForm.getUsername(), userCreateForm.getPassword1(), userCreateForm.getEmail());
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch (Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/profile/{name}")
    public String profile(@PathVariable("name") String username, Model model) {
        SiteUser siteUser = this.userService.findUser(username);
        UserPostsDTO userPosts = this.userService.findAllPost(username);

        model.addAttribute("siteUser", siteUser);
        model.addAttribute("userPostsDTO", userPosts);
        return "profile_page";
    }

    @GetMapping("/changeEmail/{username}")
    public String changeEmail(@PathVariable("username") String username, Model model, EmailChangeForm emailChangeForm) {
        SiteUser user = this.userService.findUser(username);
        model.addAttribute("user", user);
        return "change_email_form";
    }

    @RequestMapping(value = "/changeEmail/{username}", method = RequestMethod.POST)
    public String changeEmail(@PathVariable("username") String username, @RequestParam("_method") String method,
                              @Valid EmailChangeForm emailChangeForm, BindingResult bindingResult, Model model) {
        if ("PATCH".equalsIgnoreCase(method)) {
            SiteUser user = this.userService.findUser(username);
            model.addAttribute("user", user);

            if(bindingResult.hasErrors()) {
                return "change_email_form";
            }

            try {
                this.userService.changeEmail(username, emailChangeForm.getNewEmail());
            } catch (DataIntegrityViolationException e) {
                bindingResult.reject("emailChangeFailed", "사용할 수 없는 이메일입니다.");
                return "change_email_form";
            }catch (Exception e) {
                bindingResult.reject("emailChangeFailed", e.getMessage());
                return "change_email_form";
            }
        }
        return "redirect:/user/profile/%s".formatted(username);
    }

    @GetMapping("/changePassword/{username}")
    public String changePassword(@PathVariable("username") String username, Model model, ChangePasswordForm changePasswordForm) {
        SiteUser user = this.userService.findUser(username);
        model.addAttribute("user", user);
        return "change_password_form";
    }

    @RequestMapping(value = "/changePassword/{username}", method = RequestMethod.POST)
    public String changePassword(@PathVariable("username") String username, @RequestParam("_method") String method,
                                 @Valid ChangePasswordForm changePasswordForm, BindingResult bindingResult, Model model) {
        if ("PATCH".equalsIgnoreCase(method)) {
            SiteUser user = this.userService.findUser(username);
            model.addAttribute("user", user);

            if(bindingResult.hasErrors()) {
                return "change_password_form";
            }

            // 현재 비밀번호 확인
            if (!this.passwordEncoder.matches(changePasswordForm.getCurrentPassword(), user.getPassword())) {
                bindingResult.rejectValue("currentPassword", "error.currentPassword", "현재 비밀번호가 일치하지 않습니다.");
                return "change_password_form";
            }

            // 비밀번호 확인 확인
            if(!changePasswordForm.getNewPassword().equals(changePasswordForm.getConfirmPassword())){
                bindingResult.rejectValue("confirmPassword", "passwordIncorrect",
                        "비밀번호가 일치하지 않습니다.");
                return "change_password_form";
            }

            // 비밀번호 변경
            this.userService.changePassword(user, changePasswordForm.getNewPassword());
        }
        return "redirect:/user/profile/%s".formatted(username);
    }

}
