package com.mysite.sbb.global.util;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 임시 비밀번호를 이메일로 보내는 메서드
    public void sendTempPassword(String toEmail, String tempPassword) throws MailSendException, MessagingException {
        try {
            // 이메일 메시지 객체 생성
            var message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 받는 사람, 제목, 내용 설정
            helper.setTo(toEmail);
            helper.setSubject("임시 비밀번호 안내");
            helper.setText("안녕하세요,\n\n" +
                    "임시 비밀번호는 다음과 같습니다: " + tempPassword + "\n" +
                    "로그인 후 비밀번호를 변경해주세요.\n\n" +
                    "감사합니다.");

            // 이메일 보내기
            mailSender.send(message);
        } catch (MailSendException e) {
            throw new MailSendException("이메일 전송에 실패했습니다.", e);
        } catch (MessagingException e) {
            throw new MessagingException("이메일 생성에 실패했습니다.");
        }
    }
}
