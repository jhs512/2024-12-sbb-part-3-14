package com.programmers.user;

import com.programmers.exception.DuplicateUsernameException;
import com.programmers.user.dto.SignupDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class SiteUserController {
    private final SiteUserService siteUserService;

    @GetMapping("/signup")
    public String signupForm(
            SignupDto signupDto){
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid SignupDto signupDto,
            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "signup_form";
        }else if(!signupDto.password().equals(signupDto.passwordConfirmation())){
            return "signup_form";
        }
        try{
            siteUserService.save(signupDto);
        }catch (DuplicateUsernameException e){
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }
        return "redirect:login";
    }

    @GetMapping("/login")
    public String signInForm(){
        return "signIn_form";
    }
}
