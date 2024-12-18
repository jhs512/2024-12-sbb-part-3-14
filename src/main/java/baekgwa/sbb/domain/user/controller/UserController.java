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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
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
}
