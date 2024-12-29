package com.mysite.sbb.global.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public String markdown(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public static void validateUserPermission(String currentUsername, String authorUsername, String actionMessage) {
        if (!currentUsername.equals(authorUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, actionMessage + "이(가) 없습니다.");
        }
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
