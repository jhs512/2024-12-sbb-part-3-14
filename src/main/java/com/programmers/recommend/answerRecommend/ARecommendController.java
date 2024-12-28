package com.programmers.recommend.answerRecommend;

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
public class ARecommendController {
    private final ARecommendService aRecommendService;

    @ExceptionHandler(AlreadyRecommendedException.class)
    public String alreadyRecommended(Model model) {
        model.addAttribute("message", "이미 추천하였습니다.");
        return "alert";
    }

    @PostMapping("/questions/{questionId}/answers/{answerId}/recommend")
    public String recommend(
            @PathVariable("questionId") Long questionId,
            @PathVariable("answerId") Long answerId,
            Principal principal){
        aRecommendService.recommend(questionId, answerId, principal.getName());
        return "redirect:/questions/" + questionId;
    }
}
