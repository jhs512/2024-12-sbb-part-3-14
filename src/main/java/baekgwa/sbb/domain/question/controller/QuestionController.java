package baekgwa.sbb.domain.question.controller;

import baekgwa.sbb.domain.answer.form.AnswerForm;
import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.domain.question.dto.QuestionDto.DetailInfo;
import baekgwa.sbb.domain.question.dto.QuestionDto.MainInfo;
import baekgwa.sbb.domain.question.form.QuestionForm;
import baekgwa.sbb.domain.question.service.QuestionService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/detail/{id}")
    public String detail(
            Model model,
            @PathVariable("id") Integer id,
            AnswerForm answerForm) {
        QuestionDto.DetailInfo question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    @PostMapping("/create")
    public String questionCreate(
            @Valid QuestionForm questionForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        questionService.create(questionForm.getSubject(), questionForm.getContent(), principal.getName());
        return "redirect:/question/list";
    }

    @GetMapping("/list")
    public String list(
            Model model,
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value="size", defaultValue = "10") int size) {
        Page<QuestionDto.MainInfo> paging = questionService.getList(page, size);
        model.addAttribute("paging", paging);
        return "question_list";
    }
}
