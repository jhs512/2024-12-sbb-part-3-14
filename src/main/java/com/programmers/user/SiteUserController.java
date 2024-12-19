package com.programmers.user;

import com.programmers.user.dto.SignupDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
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
        }
        return "signin_form";
    }
}
