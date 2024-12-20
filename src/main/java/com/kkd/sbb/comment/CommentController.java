package com.kkd.sbb.comment;

import com.kkd.sbb.answer.Answer;
import com.kkd.sbb.answer.AnswerService;
import com.kkd.sbb.question.Question;
import com.kkd.sbb.question.QuestionService;
import com.kkd.sbb.user.SiteUser;
import com.kkd.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question/{id}")
    public String questionComment(Model model, @PathVariable Integer id,
                                  @Valid CommentForm commentForm, BindingResult bindingResult,
                                  Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Comment comment = this.commentService.create(commentForm.getContent(), question, null, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String answerComment(Model model, @PathVariable Integer id,
                                  @Valid CommentForm commentForm, BindingResult bindingResult,
                                  Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        Question question = answer.getQuestion();
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Comment comment = this.commentService.create(commentForm.getContent(), question, answer, siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s", question.getId(),id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(Model model, @PathVariable Integer id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }
}
