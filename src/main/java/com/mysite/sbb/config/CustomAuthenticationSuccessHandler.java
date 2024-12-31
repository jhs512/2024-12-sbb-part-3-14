package com.mysite.sbb.config;


import com.mysite.sbb.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(@Lazy UserService userService) {
        this.userService = userService;
    }


    @Override
    public void onAuthenticationSuccess (
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (authentication != null) {
            System.out.println("Authentication principal: " + authentication.getPrincipal());
        } else {
            System.out.println("Authentication object is null");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();


        //  임시PW 상태 확인
        if (userService.isUsingTemporaryPassword(username)) {
            // 비밀번호 변경 화면으로 리다이렉트
            response.sendRedirect("/password/change");
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
