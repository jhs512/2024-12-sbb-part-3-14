package com.mysite.sbb.web.user;

import com.mysite.sbb.domain.user.UserServiceImpl;
import com.mysite.sbb.web.user.dto.request.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.mysite.sbb.global.common.constant.PageConstants.LOGIN_FORM_VIEW;
import static com.mysite.sbb.global.common.constant.PageConstants.SIGNUP_FORM_VIEW;

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

}
