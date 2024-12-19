package com.programmers.question;

import com.programmers.answer.Answer;
import com.programmers.answer.AnswerService;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("questions")
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String exceptionHandle(Model model, MethodArgumentNotValidException e) {
        return "redirect:/questions/all";
    }

    @GetMapping("/create")
    public String registerForm(QuestionRegisterRequestDto requestDto) {
        return "register";
    }

    @PostMapping("/create")
    public String registerQuestion(
            Principal principal,
            @Valid @ModelAttribute QuestionRegisterRequestDto requestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        Question question = questionService.createQuestion(requestDto, principal);
        return "redirect:/questions/" + question.getId();
    }

    @GetMapping("/all")
    public String findAllQuestions(
            Model model,
            @Valid @ModelAttribute PageRequestDto pageRequestDto) {
        Page<Question> questionPage = questionService.findAllQuestions(pageRequestDto);
        model.addAttribute("questionPage", questionPage);
        return "list";
    }

    @GetMapping("/{questionId}")
    public String findQuestionById(
            @PathVariable Long questionId,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            Model model) {
        Question question = questionService.findQuestionById(questionId);
        Page<Answer> answerPage = answerService.getAnswers(pageRequestDto);
        model.addAttribute("question", question);
        model.addAttribute("answerPage", answerPage);
        return "question_detail";
    }
}
