package org.example.jtsb02.comment.controller;

import static org.example.jtsb02.common.util.UserUtil.getUsernameFromPrincipal;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.service.AnswerService;
import org.example.jtsb02.comment.form.CommentForm;
import org.example.jtsb02.comment.service.AnswerCommentService;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.service.MemberService;
import org.example.jtsb02.question.dto.QuestionDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment/answer")
public class AnswerCommentController {

    private final AnswerCommentService answerCommentService;
    private final AnswerService answerService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createQuestionComment(@PathVariable("id") Long id, @Valid CommentForm commentForm,
        BindingResult bindingResult, Model model, Principal principal) {
        AnswerDto answer = answerService.getAnswer(id);
        QuestionDto question = answer.getQuestion();
        if(bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question/detail";
        }
        String memberId = getUsernameFromPrincipal(principal);
        MemberDto member = memberService.getMember(memberId);
        answerCommentService.createAnswerComment(id, commentForm, member);
        return String.format("redirect:/question/detail/%s", question.getId());
    }
}
