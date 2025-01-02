package com.mysite.sbb.user;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.qustion.QuestionForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginUser() {
        return "/user/user_login";
    }
    @GetMapping("/signup")
    public String signupUser(UserForm userForm) {
        return "/user/user_signup";
    }
    @GetMapping("/user_profile")
    public String profile(Model model,UserPasswordForm userPasswordForm,Principal principal){
        System.out.println("sadasdasdasdsadsaasdsadsadasasd");
        SiteUser siteUser = userService.getSiteUser(principal.getName());
        System.out.println(principal.getName());
        System.out.println("sadasdasdasdsadsaasdsadsadasasd");
        model.addAttribute("user", siteUser );
        return "/user/user_profile";
    }
    @PostMapping("/change_password")
    public String changePassword(Model model, @Valid UserPasswordForm userPasswordForm, BindingResult bindingResult,Principal principal){
        SiteUser siteUser = userService.getSiteUser(principal.getName());
        model.addAttribute("user", siteUser );
        if (bindingResult.hasErrors()) {
            return "/user/user_profile";
        }
        if (!userService.checkPassword(userPasswordForm.getPassword(),siteUser)) {
            bindingResult.rejectValue("password", "passwordInCorrect",
                    "패스워드가 일치하지 않습니다.");
            return "/user/user_profile";
        }
        if (!userPasswordForm.getNewpassword().equals(userPasswordForm.getNewpasswordcheck())) {
            bindingResult.rejectValue("newpassword", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "/user/user_profile";
        }
        bindingResult.rejectValue("newpasswordcheck","passwordCorrect",
                "비밀번호 변경 완료");
        userService.changePassword(userPasswordForm.getNewpassword(),siteUser);
        return "/user/user_profile";
    }
    @PostMapping("/signup_run")
    public String createUser(@Valid UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/user/user_signup";
        }
        if (!userForm.getPassword().equals(userForm.getPwCheck())) {
            bindingResult.rejectValue("pwCheck", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "/user/user_signup";
        }
        try {
            this.userService.create(userForm.getUserName(),userForm.getPassword(),userForm.getEmail());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "/user/user_signup";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "/user/user_signup";
        }
        return "redirect:/question/list/1";
    }



}
