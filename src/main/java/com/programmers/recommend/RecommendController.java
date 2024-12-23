package com.programmers.recommend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;


    @PostMapping("/questions/{questionId}/recommend")
    public String recommend(
            @PathVariable("questionId") Long questionId,
            Principal principal){
        recommendService.recommend(questionId, principal.getName());
        return "redirect:/";
    }
}
