package com.mysite.sbb.web.view;

import com.mysite.sbb.domain.user.service.UserServiceImpl;
import com.mysite.sbb.web.api.v1.user.dto.request.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static com.mysite.sbb.global.constant.View.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserViewController {

    private final UserServiceImpl userService;

    @GetMapping("/signup")
    public String showSignupForm(UserRequestDTO userRequestDTO) {
        return User.SIGNUP;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return User.LOGIN;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return User.FORGOT_PASSWORD;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfileForm(Model model, Principal principal) {
        // TODO: 모든 질문/댓글/답변 나오게 하기
        model.addAttribute("user", userService.getProfile(principal.getName()));
        return User.PROFILE;
    }

}
