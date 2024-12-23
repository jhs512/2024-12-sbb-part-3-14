package org.example.jtsb02.question.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        Long questionId = questionService.createQuestion(questionForm);
        return String.format("redirect:/question/detail/%s", questionId);
    }

    @GetMapping("/list")
    public String getQuestions(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Page<QuestionDto> questions = questionService.getQuestions(page);
        model.addAttribute("paging", questions);
        return "question/list";
    }

    @GetMapping("/detail/{id}")
    public String getQuestion(@PathVariable("id") Long id, AnswerForm answerForm, Model model) {
        QuestionDto question = questionService.getQuestionWithHitsCount(id);
        model.addAttribute("question", question);
        model.addAttribute("answerForm", answerForm);
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

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Long id) {
        questionService.deleteQuestion(id);
        return "redirect:/question/list";
    }
}
