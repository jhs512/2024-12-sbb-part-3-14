package com.programmers.question;

import com.programmers.page.dto.PageRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("question")
public class QuestionController {
    private final QuestionService questionService;


    @GetMapping("/list")
    public String findAllQuestions(
            Model model,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/question/list";
        }else {
            Page<Question> questionPage = questionService.findAllQuestions(pageRequestDto);
            model.addAttribute("questionList", questionPage.getContent());
            return "question/list";
        }
    }
}
