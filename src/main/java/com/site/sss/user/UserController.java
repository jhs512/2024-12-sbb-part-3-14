package com.site.sss.user;

import jakarta.validation.Valid;

import lombok.Getter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

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
    public String signup(@Valid UserCreateForm userCreateForm,BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "signup_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2","passwordInCorrect","2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public String userInfo(Principal principal, Model model, UserModifyForm userModifyForm) {

        SiteUser user=this.userService.getUser(principal.getName());
        SiteUserDTO siteUserDTO= userService.getUserInfo(user);
        model.addAttribute("user",siteUserDTO);

        return "user_info";
    }

    @PostMapping("/modifyName")
    public String nameModify(UserModifyForm userModifyForm,Model model , Principal principal){

        SiteUser user =this.userService.getUser(principal.getName());
        model.addAttribute("user", user);
        this.userService.modifyName(user, userModifyForm.getName());
        return "user_info";
    }

    @GetMapping("/passwordCheckPage")
    public String passwordCheck(UserModifyForm userModifyForm) {

        return "passwordCheckPage";
    }

    @PostMapping("/modifyPassword")
    public String passwordModify(UserModifyForm userModifyForm,Model model
                                ,Principal principal) {

        SiteUser user =this.userService.getUser(principal.getName());
        model.addAttribute("user", user);

        if(userService.checkPasswordMatch(userModifyForm.getCheckPassword(),user.getPassword())) {
            userService.modifyPassword(user,userModifyForm.getNewPassword());
            return "user_info";
        }
        //필요시 model에 틀렸다는 메세지 보내자
        return "password_check";
    }

    @GetMapping("/delete")
    public String userDelete(Model model,Principal principal) {
        SiteUser user =this.userService.getUser(principal.getName());
        model.addAttribute("user", user);
        this.userService.deleteUser(principal.getName());
        SecurityContextHolder.clearContext();

        return "redirect:/";
    }
}
