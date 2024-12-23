package com.mysite.sbb.user;

import com.mysite.sbb.jwt.JwtTokenProvider;
import com.mysite.sbb.util.PasswordUtil;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.email.Email;
import com.mysite.sbb.email.EmailService;
import com.mysite.sbb.question.QuestionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.security.Security;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "signup_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue(
                    "password2",
                    "passwordIncorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(
                    userCreateForm.getUsername(),
                    userCreateForm.getEmail(),
                    userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch(Exception e){
            e.printStackTrace();;
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/find-password")
    public String findPassword(UserFindPasswordForm userFindPasswordForm) {
        return "find_password_form";
    }

    @PostMapping("/find-password")
    public String findPassword(@Valid UserFindPasswordForm userFindPasswordForm,
                               BindingResult bindingResult){

        if(bindingResult.hasErrors()) {
            return "find_password_form";
        }

        String username = userFindPasswordForm.getUsername();
        if(!userService.existsUser(username)){
            bindingResult.reject("invalid_user", "유효하지 않은 ID 입니다.");
            return "find_password_form";
        }
        SiteUser user = userService.getUser(username);
        if(!userFindPasswordForm.getEmail().equals(user.getEmail())) {
            bindingResult.reject("invalid_email", "유효하지 않은 이메일 입니다.");
            return "find_password_form";
        }

        // 임시 비밀번호 생성
        String tempPassword = PasswordUtil.createTempPassword(10);
        // 이메일로 임시 비밀번호 전송
        Email email = new Email(user.getEmail(), "임시 비밀번호입니다" , String.format("임시 비밀번호 : %s", tempPassword));
        emailService.sendEmail(email);
        // 임시 비밀번호로 비밀번호 변경
        userService.changePassword(user, tempPassword);

        return "redirect:/user/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/change-password")
    public String changePassword(UserChangePasswordForm userChangePasswordForm) {
        return "change_password_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public String changePassword(@Valid UserChangePasswordForm userChangePasswordForm,
                                 BindingResult bindingResult,
                                 Principal principal) {

        if (bindingResult.hasErrors()) {
            return "change_password_form";
        }

        String username = principal.getName();
        SiteUser user = userService.getUser(username);
        if (!userService.validatePassword(user, userChangePasswordForm.getOldPassword())) {
            bindingResult.reject("invalidPassword", "유효하지 않은 비밀번호입니다.");
            return "change_password_form";
        }

        if (!userChangePasswordForm.getNewPassword1().equals(userChangePasswordForm.getNewPassword2())) {
            bindingResult.reject("passwordCheckFailed", "비밀번호 확인 실패하였습니다.");
            return "change_password_form";
        }

        userService.changePassword(user, userChangePasswordForm.getNewPassword1());

        return "redirect:/user/logout";
    }
}
