package com.mysite.sbb.user.controller;

import com.mysite.sbb.email.service.EmailService;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.entity.form.UserChangePasswordForm;
import com.mysite.sbb.user.entity.form.UserCreateForm;
import com.mysite.sbb.user.entity.form.UserFindForm;
import com.mysite.sbb.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.metamodel.mapping.SqlExpressible;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.SQLException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/login")
    public String login() {
        return "form/login_form";
    }

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "form/signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form/signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "form/signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getPassword1(), userCreateForm.getEmail());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
            return "form/signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "form/signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        SiteUser siteUser = userService.findByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        model.addAttribute("siteUser", siteUser);

        return "user/user_profile";
    }

    @GetMapping("/findPassword")
    public String findPassword(UserFindForm userFindForm) {
        return "form/findPassword_form";
    }

    @PostMapping("/sendTemporaryPassword")
    @Transactional
    public String sendTemporaryPassword(@Valid UserFindForm userFindForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "form/findPassword_form";
        }

        SiteUser siteUser = userService.findByUsername(userFindForm.getUsername()).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));;
        if (!siteUser.getEmail().equals(userFindForm.getEmail())) {
            bindingResult.rejectValue("email", "emailFail", "이메일이 맞지 않습니다.");
            return "form/findPassword_form";
        }
        String temporaryPassword = "123123";
        String subject = "임시 비밀번호 안내";
        String body = "안녕하세요, \n\n" +
                "요청하신 임시 비밀번호는 다음과 같습니다:\n" +
                temporaryPassword + "\n\n" +
                "감사합니다.";

        //emailService.sendEmail(userFindForm.getEmail(), subject, body);
        siteUser.setPassword(temporaryPassword);
        redirectAttributes.addFlashAttribute("username", siteUser.getUsername());
        return "redirect:/user/changePassword";
    }

    @GetMapping("/changePassword")
    public String changePassword(UserChangePasswordForm userChangePasswordForm, @ModelAttribute("username") String username) {
        userChangePasswordForm.setUsername(username);
        return "form/changePassword_form";
    }

    @PostMapping("/changePassword")
    @Transactional
    public String changePassword(@Valid UserChangePasswordForm userChangePasswordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form/changePassword_form";
        }
        SiteUser siteUser = userService.findByUsername(userChangePasswordForm.getUsername()).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));;
        if (!userChangePasswordForm.getPassword1().equals(siteUser.getPassword())) {
            bindingResult.rejectValue("password1", "password1Fail", "현재 비밀번호가 일치하지 않습니다.");
            return "form/changePassword_form";
        }
        if (!userChangePasswordForm.getPassword2().equals(userChangePasswordForm.getPassword3())) {
            bindingResult.rejectValue("password3", "password3Fail", "2개의 패스워드가 일치하지 않습니다.");
            return "form/changePassword_form";
        }
        siteUser.setPassword(userChangePasswordForm.getPassword2());
        return "redirect:/";
    }
}
