package com.programmers.question;

import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("questions")
public class QuestionController {
    private final QuestionService questionService;

    @ExceptionHandler
    public String exceptionHandle(Model model, ValidationException e) {
        return "redirect:/question/list";
    }

    @GetMapping("/create")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/create")
    public String registerQuestion(
            @Valid @ModelAttribute QuestionRegisterRequestDto requestDto,
            Model model) {
        Question question = questionService.createQuestion(requestDto);
        model.addAttribute("question", question);
        return "redirect:/question/list";
    }

    @GetMapping("/all")
    public String findAllQuestions(
            Model model,
            @Valid @ModelAttribute PageRequestDto pageRequestDto) {
        Page<Question> questionPage = questionService.findAllQuestions(pageRequestDto);
        model.addAttribute("questionList", questionPage.getContent());
        return "list";
    }

    @GetMapping("/{questionId}")
    public String findQuestionById(
            @PathVariable Long questionId,
            Model model) {
        Question question = questionService.findQuestionById(questionId);
            model.addAttribute("question", question);
            return "question_detail";
    }
}
