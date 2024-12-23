package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {


    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(
            Model model,
            @PathVariable("id") Integer answerId,
            @Valid CommentForm commentForm,
            BindingResult bindingResult,
            Principal principal) {

        Answer answer = answerService.getAnswer(answerId);
        SiteUser author = userService.getUser(principal.getName());

        if(bindingResult.hasErrors()) {
            model.addAttribute("question", answer.getQuestion());
            model.addAttribute("answer", answer);
            return "answer_detail";
        }

        Comment comment = this.commentService.create(answer, commentForm.getContent(), author);

        return String.format("redirect:/answer/detail/%s#comment_%s", answer.getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyComment(
        @PathVariable("id") Integer id,
        CommentForm commentForm,
        Principal principal
    ){
        Comment comment = commentService.getComment(id);
        if(!comment.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyComment(
            @PathVariable("id") Integer id,
            @Valid CommentForm commentForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        Comment comment = commentService.getComment(id);

        if(!comment.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/answer/detail/%s#comment_%s", comment.getAnswer().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteComment(
            @PathVariable("id") Integer id,
            Principal principal
    ) {
        Comment comment = commentService.getComment(id);

        if(!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        commentService.delete(comment);
        return String.format("redirect:/answer/detail/%s", comment.getAnswer().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-comment")
    public String myComment(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            Principal principal) {
        String username = principal.getName();
        Page<Comment> paging = commentService.getMyCommentList(username, page);
        model.addAttribute("paging", paging);
        return "my_comment";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(@PathVariable("id") int id,
                       Principal principal) {
        String username = principal.getName();
        SiteUser user = userService.getUser(username);
        Comment comment = commentService.getComment(id);
        this.commentService.vote(user, comment);

        return String.format("redirect:/answer/detail/%s#comment_%s", comment.getAnswer().getId(), comment.getId());
    }
}
