package org.example.jtsb02.mail.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.common.exception.EmailSendingException;
import org.example.jtsb02.mail.form.MailForm;
import org.example.jtsb02.mail.service.MailService;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;
    private final MemberService memberService;

    @PostMapping("/send")
    public String emailSend(@Valid MailForm mailForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "mail/form";
        }

        try{
            String tempPassword = mailService.generateTempPassword();
            MemberDto member = memberService.updateTempPassword(mailForm.getEmail(), tempPassword);
            mailService.sendTempPasswordEmail(member.getEmail(), member.getNickname(), tempPassword);
        } catch (DataNotFoundException e) {
            bindingResult.reject("emailNotExists", "존재하지 않는 이메일입니다.");
            return "mail/form";
        } catch (MessagingException e) {
            throw new EmailSendingException("이메일 전송에 실패했습니다.", e);
        }
        return "mail/success";
    }
}
