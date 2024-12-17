package com.mysite.sbb.user;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.qustion.QuestionForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginUser() {
        return "user_login";
    }
    @GetMapping("/signup")
    public String signupUser(UserForm userForm) {
        return "user_signup";
    }

    @PostMapping("/create")
    public String createUser(@Valid UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user_signup";
        }
        if (!userForm.getPassword().equals(userForm.getPwCheck())) {
            bindingResult.rejectValue("pwCheck", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "user_signup";
        }
        try {
            this.userService.create(userForm.getUserName(),userForm.getPassword(),userForm.getEmail());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "user_signup";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "user_signup";
        }
        return "redirect:/question/list";
    }
}
