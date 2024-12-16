package com.programmers.question;

import com.programmers.page.dto.PageRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("question")
public class QuestionController {
    private final QuestionService questionService;


    public String findAllQuestions(
            Model model,
            @Valid PageRequestDto pageRequestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:question/questions";
        }else {
            Page<Question> questions = questionService.findAllQuestions(pageRequestDto);
            model.addAttribute("questions", questions);
            return "question/list";
        }
    }
}
