package org.example.jtsb02.question.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return "question/form/create";
    }

    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question/form/create";
        }
        questionService.createQuestion(questionForm);
        return "redirect:/question/list";
    }

    @GetMapping("/list")
    public String getQuestions(Model model) {
        List<QuestionDto> questions = questionService.getQuestions();
        model.addAttribute("questions", questions);
        return "question/list";
    }

    @GetMapping("/detail/{id}")
    public String getQuestion(@PathVariable("id") Long id, Model model) {
        QuestionDto question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }

    @GetMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Long id, Model model,
        QuestionForm questionForm) {
        QuestionDto question = questionService.getQuestion(id);
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        model.addAttribute("questionForm", questionForm);
        return "question/form/modify";
    }

    @PostMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Long id, @Valid QuestionForm questionForm,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question/form/modify";
        }
        questionService.modifyQuestion(id, questionForm);
        return String.format("redirect:/question/detail/%s", id);
    }
}
