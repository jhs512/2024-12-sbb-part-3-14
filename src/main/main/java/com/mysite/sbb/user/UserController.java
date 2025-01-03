package com.mysite.sbb.user;


import com.mysite.sbb.PasswordGenerator;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.comment.CommentService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.model.IModel;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final JavaMailSender mailSender;

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
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("email", userService.getUser(username).getEmail());
        model.addAttribute("username", username);
        model.addAttribute("questionList", questionService.getListByAuthor(5, username));
        model.addAttribute("answerList", answerService.getListByAuthor(5, username));
        model.addAttribute("commentList", commentService.getListByAuthor(5, username));

        return "profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String modifyPassword(PasswordModifyForm passwordModifyForm) {
        return "modify_password_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modifyPassword(@Valid PasswordModifyForm passwordModifyForm, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            return "modify_password_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if (!this.userService.isSamePassword(siteUser, passwordModifyForm.getPassword1())) {
            bindingResult.rejectValue("password1", "passwordIncorrect", "이전 비밀번호와 일치하지 않습니다.");
            return "modify_password_form";
        }

        if (!passwordModifyForm.getNewPassword1().equals(passwordModifyForm.getNewPassword2())) {
            bindingResult.rejectValue("NewPassword2", "newPasswordIncorrect", "2개의 비밀번호가 일치하지 않습니다.");
        }

        try {
            userService.modifyPassword(siteUser, passwordModifyForm.getNewPassword1());
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("passwordUpdateFail", e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/find")
    public String findPassword(FindPasswordForm findPasswordForm) {
        return "find_password";
    }

    @PostMapping("/find")
    public String findPassword(@Valid FindPasswordForm findPasswordForm, BindingResult bindingResult, Model model) {
        SiteUser siteUser = this.userService.getUserByEmail(findPasswordForm.getEmail());
        if (bindingResult.hasErrors()) {
            bindingResult.rejectValue("email", "email doesnt exist", "이메일이 존재하지 않습니다.");
            model.addAttribute("emailNotExist", true);
            return "find_password";
        }
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(findPasswordForm.getEmail());
        simpleMailMessage.setSubject("비밀번호 찾기 메일"); // 제목 설정
        StringBuffer stringBuffer = new StringBuffer();
        String newPassword = PasswordGenerator.generateTemporaryPassword(); // 임시 비밀번호 생성
        stringBuffer.append(findPasswordForm.getEmail()).append("의 비밀번호를 새롭게 발급하였습니다.")
                .append("새 비밀번호는 ").append(newPassword).append("입니다\n")
                .append("새 비밀번호를 통해 로그인 해주세요.");
        simpleMailMessage.setText(stringBuffer.toString()); // 내용 설정
        this.userService.modifyPassword(siteUser, newPassword);
        mailSender.send(simpleMailMessage);
        model.addAttribute("success", true);
        return "find_password";
    }

}
