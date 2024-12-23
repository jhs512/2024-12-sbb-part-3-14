package baekgwa.sbb.global.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EMailSender {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content)
            throws MailException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 이메일 보내는 사람 설정
        helper.setFrom("ksu9801@naver.com");  // 네이버 이메일 주소
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        // 이메일 전송
        mailSender.send(message);
    }
}
