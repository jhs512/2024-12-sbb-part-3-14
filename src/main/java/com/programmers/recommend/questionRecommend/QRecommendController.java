package com.programmers.recommend.questionRecommend;

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
public class QRecommendController {
    private final com.programmers.recommend.questionRecommend.QRecommendService QRecommendService;

    @ExceptionHandler(AlreadyRecommendedException.class)
    public String alreadyRecommended(Model model) {
        model.addAttribute("message", "이미 추천하였습니다.");
        return "alert";
    }

    @PostMapping("/questions/{questionId}/recommend")
    public String recommend(
            @PathVariable("questionId") Long questionId,
            Principal principal){
        QRecommendService.recommend(questionId, principal.getName());
        return "redirect:/questions/" + questionId;
    }
}
