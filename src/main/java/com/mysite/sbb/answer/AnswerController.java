package com.mysite.sbb.answer;
import java.util.List;

import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.qustion.QuestionForm;
import com.mysite.sbb.qustion.QuestionRepository;
import com.mysite.sbb.qustion.QuestionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/answer")
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult) {
        Question question = this.questionService.getQuestion(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("content", question);
            return "qustion_detail";
        }
        answerService.create(question, answerForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @GetMapping("/lists")
    public String createAnswer(Model model) {
        List<Answer> answers = this.answerService.getList();
        model.addAttribute("answerList",answers);
        return "answer_list";
    }


}
