package org.example.jtsb02.comment.controller;

import static org.example.jtsb02.common.util.UserUtil.checkUserPermission;
import static org.example.jtsb02.common.util.UserUtil.getUsernameFromPrincipal;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.comment.form.CommentForm;
import org.example.jtsb02.comment.service.QuestionCommentService;
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
@RequestMapping("/comment/question")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;
    private final QuestionService questionService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createQuestionComment(@PathVariable("id") Long id, @Valid CommentForm commentForm,
        BindingResult bindingResult, Model model, Principal principal) {
        QuestionDto question = questionService.getQuestionWithHitsCount(id, 1, "");
        if(bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("answerForm", new AnswerForm());
            model.addAttribute("commentForm", commentForm);
            return "question/detail";
        }
        String memberId = getUsernameFromPrincipal(principal);
        MemberDto member = memberService.getMember(memberId);
        questionCommentService.createQuestionComment(id, commentForm, member);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyQuestionComment(@PathVariable("id") Long id, CommentForm commentForm, Model model) {
        CommentDto comment = questionCommentService.getQuestionComment(id);
        commentForm.setContent(comment.getContent());
        model.addAttribute("commentForm", commentForm);
        return "comment/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyQuestionComment(@PathVariable("id") Long id, @Valid CommentForm commentForm,
        BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment/form";
        }

        String username = getUsernameFromPrincipal(principal);
        CommentDto comment = questionCommentService.getQuestionComment(id);
        checkUserPermission(username, comment.getAuthor().getMemberId(), "수정");

        questionCommentService.modifyQuestionComment(id, commentForm);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteQuestionComment(@PathVariable("id") Long id, Principal principal) {
        String username = getUsernameFromPrincipal(principal);
        CommentDto comment = questionCommentService.getQuestionComment(id);
        checkUserPermission(username, comment.getAuthor().getMemberId(), "삭제");

        questionCommentService.deleteQuestionComment(id);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteQuestionComment(@PathVariable("id") Long id, Principal principal) {
        CommentDto comment = questionCommentService.getQuestionComment(id);
        MemberDto member = memberService.getMember(getUsernameFromPrincipal(principal));

        questionCommentService.voteQuestionComment(id, member);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }
}
