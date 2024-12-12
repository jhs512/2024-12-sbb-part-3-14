package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question/{id}")
    public String createCommentAtQuestion(@Valid CommentForm commentForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal, Model model) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Comment comment = this.commentService.createAtComment(commentForm.getContent(), question, siteUser);
        return String.format("redirect:/question/detail/%s#comment_%s", comment.getQuestion().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String createCommentAtAnswer(@Valid CommentForm commentForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal, Model model) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer", answer);
            return "question_detail";
        }
        Comment comment = this.commentService.createAtAnswer(commentForm.getContent(), answer, siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s#comment_%s", answer.getQuestion().getId(), comment.getAnswer().getId(), answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentForm commentForm, @PathVariable("id") Integer id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        commentForm.setContent(commentForm.getContent());
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(@Valid CommentForm commentForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment comment = this.commentService.getComment(id);
        if(!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/question/detail/%s#comment_%s", comment.getQuestion().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/answer/modify/{id}")
    public String commentModifyAtAnswer(@Valid CommentForm commentForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment comment = this.commentService.getComment(id);
        if(!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/question/detail/%s#answer%s#comment_%s", comment.getQuestion().getId(), comment.getAnswer().getId(),comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDeleteAtQuestion(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/answer/{id}")
    public String commentDeleteAtAnswer(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", comment.getAnswer().getQuestion().getId());
    }
}
