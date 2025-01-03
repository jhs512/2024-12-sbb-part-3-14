package com.mysite.sbb.user;

import com.mysite.sbb.Answer.Answer;
import com.mysite.sbb.Comment.Comment;
import com.mysite.sbb.Question.Question;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    // 로그인
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    // 비밀번호 찾기 폼
    @GetMapping("/reset-password")
    public String resetPasswordForm() {
        return "reset_password_form";
    }

    // 비밀번호 찾기 요청 처리
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email) {
        try {
            userService.resetPassword(email);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return "redirect:/user/reset-password?error=emailNotFound";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/user/reset-password?error=serverError";
        }

        return "redirect:/user/login?resetPasswordSuccess";
    }

    // 비밀번호 변경 폼
    @GetMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public String changePasswordForm(Principal principal) {
        if (principal == null) {
            System.out.println("현재 사용자: 인증되지 않음");
            return "redirect:/user/login?error=notAuthenticated";
        }

        String username = principal.getName();
        System.out.println("현재 사용자: " + username);
        return "change_password_form";
    }

    // 비밀번호 변경 요청 처리
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public String changePassword(Principal principal,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword1") String newPassword1,
                                 @RequestParam("newPassword2") String newPassword2) {
        if (principal == null) {
            return "redirect:/user/login?error=notAuthenticated";
        }

        if (!newPassword1.equals(newPassword2)) {
            return "redirect:/user/change-password?error=passwordMismatch";
        }

        try {
            String username = principal.getName();
            userService.changePassword(username, currentPassword, newPassword1);

            // Spring Security 인증 정보 갱신
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    username, null, SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/user/change-password?error=" + e.getMessage();
        }

        return "redirect:/";
    }

    //프로필
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String userProfile(Principal principal, Model model) {
        String username = principal.getName(); // 현재 로그인 사용자 이름
        SiteUser user = userService.getUser(username);

        List<Question> questions = userService.getQuestionsByUser(username);
        List<Answer> answers = userService.getAnswersByUser(username);
        List<Comment> comments = userService.getCommentsByUser(username);

        model.addAttribute("user", user);
        model.addAttribute("questions", questions);
        model.addAttribute("answers", answers);
        model.addAttribute("comments", comments);

        return "profile";
    }


}