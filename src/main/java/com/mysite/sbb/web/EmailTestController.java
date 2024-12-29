package com.mysite.sbb.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailTestController {

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @GetMapping("/test-email")
    public String testEmail() {
        return "Username: " + emailUsername + ", Password: " + emailPassword;
    }
}

