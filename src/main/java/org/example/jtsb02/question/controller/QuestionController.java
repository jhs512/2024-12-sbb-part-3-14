package org.example.jtsb02.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/create")
    public String createQuestion(Model model, QuestionForm questionForm) {
        model.addAttribute("questionForm", questionForm);
        return "question_form";
    }

    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "question_form";
        }
        questionService.createQuestion(questionForm);
        return "redirect:/";
    }

}
