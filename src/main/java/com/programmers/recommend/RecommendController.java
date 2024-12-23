package com.programmers.recommend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;


    @GetMapping("/questions/{questionId}/recommend")
    public String recommend(
            @PathVariable("questionId") Long questionId,
            Principal principal){
        recommendService.recommend(questionId, principal.getName());
        return "redirect:/questions/" + questionId;
    }

    @GetMapping("/questions/{questionId}/recommend/delete")
    public String deleteRecommend(
            @PathVariable("questionId") Long questionId,
            Principal principal
    ){
        recommendService.deleteRecommend(questionId, principal.getName());
        return "redirect:/questions/" + questionId;
    }
}
