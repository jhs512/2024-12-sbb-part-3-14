package com.mysite.sbb.mail;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

import static com.mysite.sbb.mail.EmailService.code;

@RequestMapping("/send-mail")
@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/inputPage")
    public String inputPage(Model model, EmailResponseForm emailResponseForm){
        return "/email/psFind";
    }
    @PostMapping("/sendEmail")
    public String sendEmail(Model model, @Valid EmailPostForm emailPostForm){
        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailPostForm.getEmail())
                .subject("코드 발송")
                .build();

        model.addAttribute("email", emailPostForm.getEmail());
        code.put(emailPostForm.getEmail(),emailService.sendMail(emailMessage, "password"));
        return "/email/psFind";
    }
    @GetMapping("/sendEmail")
    public String sendEmail(EmailPostForm emailResponseForm){
        return "/email/inputEmail";
    }
    @PostMapping("/codeCheck")
    public String codeCheck(Model model, @Valid EmailResponseForm emailResponseForm){
        String email = emailResponseForm.getEmail();
        if(code.containsKey(email)){
            if(code.get(email).equals(emailResponseForm.getCode())){
                String password = emailService.getCodeAndPasswordChange(email);
                EmailMessage emailMessage = EmailMessage.builder()
                        .to(email)
                        .subject("비밀번호")
                        .message("비밀번호:" + password)
                        .build();
                emailService.sendMail(emailMessage, "password");
                code.remove(email);
                return "redirect:/question/list/1";
            }

            model.addAttribute("email", email);
            return "redirect:/question/list/1";
        }

        model.addAttribute("email", email);
        return "redirect:/question/list/1";
    }
}