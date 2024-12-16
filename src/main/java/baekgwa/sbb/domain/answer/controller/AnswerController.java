package baekgwa.sbb.domain.answer.controller;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import baekgwa.sbb.domain.answer.form.AnswerForm;
import baekgwa.sbb.domain.answer.service.AnswerService;
import baekgwa.sbb.domain.question.dto.QuestionDto;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/create/{id}")
    public String createAnswer(
            Model model,
            @PathVariable("id") Integer id,
            @Valid AnswerForm answerForm,
            BindingResult bindingResult,
            Principal principal) {
        if (bindingResult.hasErrors()) {
            String loginUsername = principal == null ? null : principal.getName();
            QuestionDto.DetailInfo question = answerService.getQuestionByIdAndAnswers(id, loginUsername);
            model.addAttribute("question", question);
            return "question_detail";
        }
        answerService.create(id, answerForm.getContent(), principal.getName());
        return String.format("redirect:/question/detail/%s", id);
    }

    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        AnswerDto.AnswerInfo answer = answerService.getAnswer(id);
        if (!answer.getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
            @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Integer modifiedQuestionId = answerService.modifyAnswer(id, principal.getName(), answerForm.getContent());
        return String.format("redirect:/question/detail/%s", modifiedQuestionId);
    }

    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Integer questionId = answerService.deleteAnswer(id, principal.getName());
        return String.format("redirect:/question/detail/%s", questionId);
    }

    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        Integer questionId = answerService.vote(id, principal.getName());
        return String.format("redirect:/question/detail/%s", questionId);
    }

    @GetMapping("/vote/cancel/{id}")
    public String questionVoteCancel(
            Principal principal,
            @PathVariable("id") Integer id) {
        Integer questionId = answerService.voteCancel(id, principal.getName());
        return String.format("redirect:/question/detail/%s", questionId);
    }
}
