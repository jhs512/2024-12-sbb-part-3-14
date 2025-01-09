package com.programmers.question;

import com.programmers.answer.AnswerService;
import com.programmers.answer.dto.AnswerViewDto;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionModifyRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import com.programmers.question.dto.QuestionViewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("questions")
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String exceptionHandle(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
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
        Question question = questionService.createQuestion(requestDto, principal.getName());
        return "redirect:/questions/" + question.getId();
    }

    @GetMapping("/all")
    public String list(Model model,
                       @Valid @ModelAttribute PageRequestDto pageRequestDto,
                       @RequestParam(value = "s", required = false) String s) {
        Page<QuestionViewDto> questionPage = questionService.getQuestionPageBySearch(pageRequestDto, s);
        model.addAttribute("questionPage", questionPage);
        model.addAttribute("s", s);
        return "list";
    }

    @GetMapping("/{questionId}")
    public String findQuestionById(
            @PathVariable Long questionId,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            Model model) {
        QuestionViewDto question = questionService.findQuestionById(questionId);
        Page<AnswerViewDto> answerPage = answerService.getAnswers(questionId, pageRequestDto);
        model.addAttribute("question", question);
        model.addAttribute("answerPage", answerPage);
        return "question_detail";
    }


    @GetMapping("/{questionId}/modify")
    public String modifyForm(
            @PathVariable Long questionId,
            Principal principal,
            Model model
    ){
        Question question = questionService.findQuestionByIdAndUsername(questionId, principal.getName());

        QuestionRegisterRequestDto requestDto = QuestionRegisterRequestDto.builder()
                .subject(question.getSubject())
                .content(question.getContent())
                .build();
        model.addAttribute("questionRegisterRequestDto", requestDto);
        return "register";
    }

    @PostMapping("/{questionId}/modify")
    public String modifyQuestion(
            @PathVariable Long questionId,
            Principal principal,
            @Valid @ModelAttribute QuestionModifyRequestDto requestDto
    ){
        questionService.modifyQuestion(questionId, principal.getName(), requestDto);
        return "redirect:/questions/" + questionId;
    }

    @GetMapping("/{questionId}/delete")
    public String deleteQuestion(
            @PathVariable Long questionId,
            Principal principal) {
        questionService.deleteQuestion(questionId, principal.getName());
        return "redirect:/questions/all";
    }
}
