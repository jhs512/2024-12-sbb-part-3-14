package com.programmers.answer;

import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.exception.IdMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("/questions/{questionId}/answers")
    public String answer(@PathVariable Long questionId,
                         @ModelAttribute AnswerRegisterRequestDto requestDto) {
        if(questionId != requestDto.questionId()) {
            throw new IdMismatchException("question");
        }
        answerService.createAnswer(requestDto);
        return "redirect:/questions/" + questionId;
    }
}
