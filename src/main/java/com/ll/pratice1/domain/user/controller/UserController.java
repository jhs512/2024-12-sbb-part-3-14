package com.ll.pratice1.domain.user.controller;


import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.answer.service.AnswerService;
import com.ll.pratice1.domain.comment.Comment;
import com.ll.pratice1.domain.comment.service.CommentService;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.question.service.QuestionService;
import com.ll.pratice1.domain.user.SiteUser;
import com.ll.pratice1.domain.user.dto.MailForm;
import com.ll.pratice1.domain.user.dto.PasswordFindForm;
import com.ll.pratice1.domain.user.dto.UserCreateForm;
import com.ll.pratice1.domain.user.service.MailService;
import com.ll.pratice1.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;


    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "signup_form";
        }
        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getPassword1(),
                    userCreateForm.getEmail());
        }catch(DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login_form";
    }

    @GetMapping("/password-find")
    public String findPassword(PasswordFindForm passwordFindForm){
        return "password-find_form";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        List<Question> questionList = questionService.getList(siteUser);
        List<Answer> answerList = answerService.getAnswerList(siteUser);
        List<Comment> commentList = commentService.getCommentList(siteUser);

        model.addAttribute("commentList", commentList);
        model.addAttribute("answerList", answerList);
        model.addAttribute("questionList", questionList);
        model.addAttribute("siteUser", siteUser);

        return "profile";
    }

    @PostMapping("password-find")
    public String findPassword(@Valid PasswordFindForm passwordFindForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "password-find_form";
        }
        try {
            SiteUser siteUser = this.userService.getUser(passwordFindForm.getUsername());
            if(!siteUser.getEmail().equals(passwordFindForm.getEmail())){
                bindingResult.reject("emailNotMatch", "등록된 이메일이 아닙니다.");
                return "password-find_form";
            }
            String ramdomPassword = userService.generateNumericPassword();
            MailForm mail = this.mailService.createMail(ramdomPassword, passwordFindForm.getEmail());
            mailService.sendMail(mail);
            this.userService.updatePassword(siteUser, ramdomPassword);
            return "login_form";

        }catch(Exception e) {
            bindingResult.reject("userFindFailed", "등록된 유저가 존재하지 않습니다.");
            return "password-find_form";
        }
    }
}

