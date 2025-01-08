package com.ll.pratice1.domain.user.service;

import com.ll.pratice1.domain.user.dto.MailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private static final String title = "[SBB] 임시 비밀번호 안내 이메일입니다.";
    private static final String message = "안녕하세요. [SBB} 임시 비밀번호 안내 메일입니다. "
            + "\n" + "회원님의 임시 비밀번호는 아래와 같습니다. 로그인 후 반드시 비밀번호를 변경해주세요." + "\n";

    //properties에서 설정한 email이 자동으로 설정되었음(Mail 생성하기위해 일단 해놓음)
    private String from;

    public MailForm createMail(String tmpPassword, String to) {
        MailForm mailDto = new MailForm(from, to, title, message + tmpPassword);
        return mailDto;
    }

    public void sendMail(MailForm mailForm) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailForm.getTo());
        mailMessage.setSubject(mailForm.getTitle());
        mailMessage.setText(mailForm.getMessage());
        mailMessage.setFrom(mailForm.getFrom());
        mailMessage.setReplyTo(mailForm.getFrom());

        mailSender.send(mailMessage);
    }
}
