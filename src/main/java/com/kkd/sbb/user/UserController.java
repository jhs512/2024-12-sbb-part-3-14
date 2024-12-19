package com.kkd.sbb.user;


import com.kkd.sbb.answer.Answer;
import com.kkd.sbb.answer.AnswerService;
import com.kkd.sbb.question.Question;
import com.kkd.sbb.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;

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
            bindingResult.rejectValue("password2", "PasswordsInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
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
    public String profile(UserUpdateForm userUpdateForm, Model model, Principal principal,
                          @RequestParam(value="question-page", defaultValue="0") int questionPage,
                          @RequestParam(value="ans-page", defaultValue="0") int ansPage,
                          @RequestParam(value="question-vote-page", defaultValue="0") int questionVoterPage,
                          @RequestParam(value="ans-vote-page", defaultValue="0") int ansVoterPage) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Page<Question> wroteQuestions = this.questionService.getListByAuthor(questionPage, siteUser);
        Page<Answer> wroteAnswers = this.answerService.getListByAuthor(ansPage, siteUser);
        Page<Question> votedQuestions = this.questionService.getListByVoter(questionVoterPage, siteUser);
        Page<Answer> votedAnswers = this.answerService.getListByVoter(ansVoterPage, siteUser);
        model.addAttribute("wrote_question_paging", wroteQuestions);
        model.addAttribute("wrote_answer_paging", wroteAnswers);
        model.addAttribute("voted_question_paging", votedQuestions);
        model.addAttribute("voted_answer_paging", votedAnswers);
        model.addAttribute("username", siteUser.getUsername());
        model.addAttribute("userEmail", siteUser.getEmail());
        return "profile";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile")
    public String update(@Valid UserUpdateForm userUpdateForm, BindingResult bindingResult,
                         Model model, Principal principal) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Page<Question> wroteQuestions = this.questionService.getListByAuthor(0, siteUser);
        Page<Answer> wroteAnswers = this.answerService.getListByAuthor(0, siteUser);
        Page<Question> votedQuestions = this.questionService.getListByVoter(0, siteUser);
        Page<Answer> votedAnswers = this.answerService.getListByVoter(0, siteUser);

        model.addAttribute("wrote_question_paging", wroteQuestions);
        model.addAttribute("wrote_answer_paging", wroteAnswers);
        model.addAttribute("voted_question_paging", votedQuestions);
        model.addAttribute("voted_answer_paging", votedAnswers);
        model.addAttribute("username", siteUser.getUsername());
        model.addAttribute("userEmail", siteUser.getEmail());
        if (bindingResult.hasErrors()) {
            return this.profile(userUpdateForm, model, principal, 0, 0, 0, 0);
        }

        if(!this.userService.isMatch(userUpdateForm.getOriginPassword(), siteUser.getPassword())) {
            bindingResult.rejectValue("originPassword", "passwordInCorrect",
                    "기존 패스워드가 일치하지 않습니다.");
            return "profile";
        }
        if(!userUpdateForm.getPassword1().equals(userUpdateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "확인 패스워드가 일치하지 않습니다.");
            return "profile";
        }

        try {
            userService.update(siteUser, userUpdateForm.getPassword1());
        } catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("updateFailed", e.getMessage());
        }
        return "profile";
    }
}
