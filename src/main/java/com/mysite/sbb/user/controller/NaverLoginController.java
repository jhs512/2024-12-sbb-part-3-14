package com.mysite.sbb.user.controller;


import com.mysite.sbb.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/naver")
public class NaverLoginController {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    private final UserService userService;

    public NaverLoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String naverLogin() {
        String loginUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + naverClientId
                + "&redirect_uri=" + naverRedirectUri
                + "&state=someStateValue";
        return "redirect:" + loginUrl;
    }

    @GetMapping("/login/naver/callback")
    public String naverCallback(@RequestParam("code") String code, @RequestParam String state) {
        userService.processNaverLogin(code, state);
        return "redirect:/";
    }
}
