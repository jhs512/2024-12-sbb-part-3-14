package com.programmers.answer;

import com.programmers.answer.dto.AnswerModifyRequestDto;
import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.exception.IdMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("/questions/{questionId}/answers")
    public String answer(@PathVariable Long questionId,
                         @ModelAttribute AnswerRegisterRequestDto requestDto,
                         Principal principal) {
        answerService.createAnswer(questionId, requestDto, principal.getName());
        return String.format("redirect:/questions/%d", questionId);
    }

    @PostMapping("/questions/{questionId}/answers/{answerId}")
    public String modifyAnswer(
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @ModelAttribute AnswerModifyRequestDto requestDto,
            Principal principal){
        answerService.modifyAnswer(questionId, answerId, principal.getName(), requestDto);
        return "redirect:/questions/" + questionId;
    }
}
