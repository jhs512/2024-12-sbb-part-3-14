package baekgwa.sbb.domain.main.controller;

import baekgwa.sbb.model.user.entity.UserRole;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/sbb")
    @ResponseBody
    public String index() {
        return "안녕하세요 sbb에 오신것을 환영합니다.";
    }

    @GetMapping("/")
    public String root(
            Authentication authentication
    ) {
        if(authentication != null) {
            boolean isTemporary = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(UserRole.TEMPORARY.getValue()));

            if (isTemporary) {
                return "redirect:/user/password/modify";
            }
        }
        return "redirect:/question/list";
    }
}
