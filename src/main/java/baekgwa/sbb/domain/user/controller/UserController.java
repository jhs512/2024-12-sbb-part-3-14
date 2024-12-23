package baekgwa.sbb.domain.user.controller;

import baekgwa.sbb.domain.user.dto.UserDto.MypageInfo;
import baekgwa.sbb.domain.user.form.UserForm;
import baekgwa.sbb.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserForm.Signup signup) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid UserForm.Signup signup,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!signup.getPassword1().equals(signup.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(signup.getUsername(),
                    signup.getEmail(), signup.getPassword1());
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

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/my-page")
    public String myPage(
            Principal principal,
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size
    ) {
        MypageInfo mypageInfo = userService.getUserInfo(principal.getName(), page, size);
        model.addAttribute("mypageInfo", mypageInfo);
        return "my_page";
    }

    @GetMapping("/password/temporary")
    public String passwordTemporary() {
        return "temporary_password_form";
    }

    @PostMapping("/password/temporary")
    public String sendTemporaryPassword(
            @RequestParam("email") String email,
            Model model
    ) {
        try {
            userService.temporaryPassword(email);
        } catch (Exception e) {
            model.addAttribute("error", "메일 전송에 실패하였습니다. 잠시 후 다시 시도해 주세요.");
            return "login_form";
        }

        model.addAttribute("message", "1회용 임시 비밀번호가 발급되었습니다. 이메일을 확인해주세요.");
        return "login_form";
    }
}