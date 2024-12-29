package com.mysite.sbb.web.user;

import com.mysite.sbb.domain.user.UserServiceImpl;
import com.mysite.sbb.web.api.common.v1.user.dto.request.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static com.mysite.sbb.global.common.constant.PageConstants.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserViewController {

    private final UserServiceImpl userService;

    @GetMapping("/signup")
    public String showSignupForm(UserRequestDTO userRequestDTO) {
        return SIGNUP_FORM_VIEW;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return LOGIN_FORM_VIEW;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return FORGOT_PASSWORD_VIEW;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfileForm(Model model, Principal principal) {
        model.addAttribute("user", userService.getProfile(principal.getName()));
        return PROFILE_FORM_VIEW;
    }

}
