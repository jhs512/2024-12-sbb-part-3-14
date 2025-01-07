package com.programmers.recommend;

import com.programmers.exception.AlreadyRecommendedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;

    @ExceptionHandler(AlreadyRecommendedException.class)
    public String alreadyRecommended(Model model) {
        model.addAttribute("message", "이미 추천하였습니다.");
        return "alert";
    }

    @PostMapping("/questions/{questionId}/recommend")
    public String recommend(
            @PathVariable("questionId") Long questionId,
            Principal principal){
        recommendService.recommendQuestion(principal.getName(), questionId);
        return "redirect:/questions/" + questionId;
    }
}
