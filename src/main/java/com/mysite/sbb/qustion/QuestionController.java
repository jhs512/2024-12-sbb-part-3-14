package com.mysite.sbb.qustion;

import com.mysite.sbb.answer.AnswerForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;
    @GetMapping("/list")
    public String list(Model model){
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList",questionList);
        return "question_list";
    }
    @GetMapping("/insert")
    public String insertQuestion(QuestionForm questionForm){
        return "question_insert";
    }
    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question_insert";
        }
        this.questionService.create(questionForm.getSubject(), questionForm.getContent());
        return "redirect:/question/list";
    }
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id,AnswerForm answerForm){
        model.addAttribute("content",questionService.getQuestion(id));
        return "qustion_detail";
    }


}
