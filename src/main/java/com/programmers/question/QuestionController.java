package com.programmers.question;

import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    private final QuestionRegisterRequestDto EMPTY_REQUEST = new QuestionRegisterRequestDto(null, null);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String exceptionHandle(Model model, MethodArgumentNotValidException e) {
        return "redirect:/questions/all";
    }

    @GetMapping("/create")
    public String registerForm(Model model) {
        model.addAttribute("questionForm", EMPTY_REQUEST);
        return "register";
    }

    @PostMapping("/create")
    public String registerQuestion(
            @Valid @ModelAttribute QuestionRegisterRequestDto requestDto,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("questionForm", requestDto);
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError fieldError) {
                    model.addAttribute( fieldError.getField() + "IsInvalid", true);
                    model.addAttribute(fieldError.getField() + "Warning", fieldError.getDefaultMessage());
                }
            }
            return "register";
        }
        Question question = questionService.createQuestion(requestDto);
        model.addAttribute("question", question);
        return "redirect:/questions/all";
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
