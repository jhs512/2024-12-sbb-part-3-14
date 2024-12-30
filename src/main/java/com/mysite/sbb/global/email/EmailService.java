package com.mysite.sbb.global.email;

import com.mysite.sbb.global.constant.View;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${EMAIL_USERNAME}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendPasswordResetEmail(String to, String newPassword) throws MessagingException {
        try {
            // 1. 템플릿 데이터 생성
            Context context = new Context();
            context.setVariable("newPassword", newPassword);

            // 2. 템플릿 렌더링
            String htmlContent = templateEngine.process(View.Email.PASSWORD_RESET, context);

            // 3. 메일 메시지 생성 및 전송
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to.trim());  // 공백 제거
            helper.setSubject("새 비밀번호가 발급되었습니다.");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("이메일 전송 실패. 수신자: {}", to, e);
            throw new MessagingException("이메일 전송에 실패했습니다.", e);
        }
        }
}
