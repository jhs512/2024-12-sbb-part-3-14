package org.example.jtsb02.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.service.AnswerService;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @PostMapping("/create/{id}")
    public String createAnswer(@PathVariable("id") Long questionId, @Valid AnswerForm answerForm,
        BindingResult bindingResult, Model model) {
        QuestionDto question = questionService.getQuestion(questionId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question/detail";
        }
        answerService.createAnswer(questionId, answerForm);
        return String.format("redirect:/question/detail/%s", questionId);
    }
}
