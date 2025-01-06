package org.example.jtsb02.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 임시 비밀번호를 포함한 이메일 발송
    public void sendTempPasswordEmail(String email, String nickname, String tempPassword) throws MessagingException {
        // Thymeleaf 템플릿 렌더링
        Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("tempPassword", tempPassword);

        String emailContent = templateEngine.process("mail/template", context);

        // 이메일 생성 및 발송
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        messageHelper.setFrom(senderEmail);
        messageHelper.setTo(email);
        messageHelper.setSubject("임시 비밀번호 발급");
        messageHelper.setText(emailContent, true);

        try {
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MessagingException("Failed to send tempPassword email", e);
        }
    }

    // 비밀번호 생성 메서드
    public String generateTempPassword() {
        SecureRandom secureRandom = new SecureRandom();  // 보안 랜덤 객체 사용
        StringBuilder password = new StringBuilder();

        // 비밀번호에 포함될 문자 유형들
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";  // 소문자
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";  // 대문자
        String digits = "0123456789";  // 숫자
        String specialChars = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";  // 특수 문자

        // 각 문자 유형을 최소한 하나씩 포함하도록 보장
        password.append(lowerCase.charAt(secureRandom.nextInt(lowerCase.length())));
        password.append(upperCase.charAt(secureRandom.nextInt(upperCase.length())));
        password.append(digits.charAt(secureRandom.nextInt(digits.length())));
        password.append(specialChars.charAt(secureRandom.nextInt(specialChars.length())));

        // 나머지 자리를 랜덤하게 채워 비밀번호 길이를 맞추기
        String allCharacters = lowerCase + upperCase + digits + specialChars;  // 모든 문자 유형을 합침
        for (int i = 4; i < 12; i++) {  // 최소 12자리를 목표로
            password.append(allCharacters.charAt(secureRandom.nextInt(allCharacters.length())));
        }

        return password.toString();
    }
}
