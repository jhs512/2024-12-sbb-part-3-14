package com.example.article_site.controller;

import com.example.article_site.domain.Author;
import com.example.article_site.form.FindPasswordForm;
import com.example.article_site.form.ModifyPasswordForm;
import com.example.article_site.form.SignupForm;
import com.example.article_site.service.AnswerService;
import com.example.article_site.service.AuthorService;
import com.example.article_site.service.CommentService;
import com.example.article_site.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final QuestionService  questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    @GetMapping("/signup")
    public String signup(SignupForm signUpForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid SignupForm signUpForm,
                         BindingResult bindingResult,
                         Model model) {
        if(bindingResult.hasErrors()) {
            return "signup_form";
        }

        if(!signUpForm.getPassword1().equals(signUpForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try{
            authorService.create(signUpForm);
        }catch(Exception e) {
            return handleSignupError(e, bindingResult);
        }

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String modify(ModifyPasswordForm passwordModifyForm) {
        return "login_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modifyPassword(@Valid ModifyPasswordForm passwordModifyForm,
                                 BindingResult bindingResult,
                                 Principal principal) {
        if(bindingResult.hasErrors()) {
            return "login_modify_form";
        }

        if(!authorService.modifyPassword(passwordModifyForm, principal.getName())){
            bindingResult.reject("Wrong Input", "잘못된 입력입니다.");
            return "login_modify_form";
        }

        return "redirect:/";
    }

    @GetMapping("/find")
    public String find(FindPasswordForm findPasswordForm) {
        return "find_password_form";
    }

    @PostMapping("/find")
    public String find(@Valid FindPasswordForm findPasswordForm,
                       BindingResult bindingResult,
                       Model model) {
        if(bindingResult.hasErrors()) {
            return "find_password_form";
        }

        Optional<Author> authorOpt = authorService.checkUserPresent(
                findPasswordForm.getUsername(), findPasswordForm.getEmail());
        if(authorOpt.isEmpty()) {
            bindingResult.reject("match error", "해당하는 아이디를 찾을 수 없습니다.");
            return "find_password_form";
        }
        String newPassword = authorService.createNewPassword(authorOpt.get());
        model.addAttribute("newPassword", newPassword);
        return "password_result";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model,
                          Principal principal) {
        addProfileDataToModel(model, principal);
        return "profile_form";
    }

    private void addProfileDataToModel(Model model, Principal principal) {
        model.addAttribute("userInfo", authorService.getAuthorProfileDto(principal.getName()));
        model.addAttribute("questionList", questionService.getQuestionProfileDtoList(principal.getName()));
        model.addAttribute("answerList", answerService.getAnswerProfileDtoList(principal.getName()));
        model.addAttribute("commentList", commentService.getCommentProfileDtoList(principal.getName()));
    }

    private String handleSignupError(Exception e, BindingResult bindingResult) {
        e.printStackTrace();
        if(e instanceof DataIntegrityViolationException) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
        }else{
            bindingResult.reject("signupFailed", e.getMessage());
        }
        return "signup_form";
    }
}
