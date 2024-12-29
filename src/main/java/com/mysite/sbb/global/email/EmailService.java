package com.mysite.sbb.global.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${EMAIL_PASSWORD}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendPassowrdResetEmail(String to, String newPassword) throws MessagingException {
        // 1. 템플릿 데이터 생성
        Context context = new Context();
        context.setVariable("newPassword", newPassword);

        // 2. 템플릿 렌더링
        String htmlContent = templateEngine.process("password_email_form", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("새 비밀번호가 발급되었습니다.");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
