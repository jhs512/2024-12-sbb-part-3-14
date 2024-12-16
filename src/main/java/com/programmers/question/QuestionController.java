package com.programmers.question;

import com.programmers.page.dto.PageRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

@Controller
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    public String findAllQuestions(@Valid PageRequestDto pageRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:question/questions";
        }else {
            Page<Question> questions = questionService.findAllQuestions(pageRequestDto);
        }
    }
}
