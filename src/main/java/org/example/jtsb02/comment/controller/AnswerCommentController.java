package org.example.jtsb02.comment.controller;

import static org.example.jtsb02.common.util.UserUtil.checkUserPermission;
import static org.example.jtsb02.common.util.UserUtil.getUsernameFromPrincipal;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.service.AnswerService;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.comment.form.CommentForm;
import org.example.jtsb02.comment.service.AnswerCommentService;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.service.MemberService;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.service.QuestionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final QuestionService questionService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswerComment(@PathVariable("id") Long id, @Valid CommentForm commentForm,
        BindingResult bindingResult, Model model, Principal principal) {
        AnswerDto answer = answerService.getAnswer(id);
        QuestionDto question = questionService.getQuestionWithHitsCount(answer.getQuestion().getId(), 1, "");
        if(bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("answerForm", new AnswerForm());
            model.addAttribute("commentForm", commentForm);
            return "question/detail";
        }
        String memberId = getUsernameFromPrincipal(principal);
        MemberDto member = memberService.getMemberByMemberId(memberId);
        answerCommentService.createAnswerComment(id, commentForm, member);
        return String.format("redirect:/question/detail/%s", question.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyAnswerComment(@PathVariable("id") Long id, CommentForm commentForm, Model model) {
        CommentDto comment = answerCommentService.getAnswerComment(id);
        commentForm.setContent(comment.getContent());
        model.addAttribute("commentForm", commentForm);
        return "comment/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyAnswerComment(@PathVariable("id") Long id, @Valid CommentForm commentForm,
        BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment/form";
        }

        String username = getUsernameFromPrincipal(principal);
        CommentDto comment = answerCommentService.getAnswerComment(id);
        checkUserPermission(username, comment.getAuthor().getMemberId(), "수정");

        answerCommentService.modifyAnswerComment(id, commentForm);
        return String.format("redirect:/question/detail/%s", comment.getAnswer().getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteAnswerComment(@PathVariable("id") Long id, Principal principal) {
        String username = getUsernameFromPrincipal(principal);
        CommentDto comment = answerCommentService.getAnswerComment(id);
        checkUserPermission(username, comment.getAuthor().getMemberId(), "삭제");

        answerCommentService.deleteAnswerComment(id);
        return String.format("redirect:/question/detail/%s", comment.getAnswer().getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteAnswerComment(@PathVariable("id") Long id, Principal principal) {
        CommentDto comment = answerCommentService.getAnswerComment(id);
        MemberDto member = memberService.getMemberByMemberId(principal.getName());

        answerCommentService.voteAnswerComment(id, member);
        return String.format("redirect:/question/detail/%s", comment.getAnswer().getQuestion().getId());
    }
}
