package com.mysite.sbb.Comment;

import com.mysite.sbb.Answer.Answer;
import com.mysite.sbb.Answer.AnswerService;
import com.mysite.sbb.Question.Question;
import com.mysite.sbb.Question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/question/{id}")
    public String commentQuestion(@PathVariable("id") Integer id,
                                  @Valid CommentForm commentForm, BindingResult bindingResult,
                                  Principal principal, Model model) {
        Question question = this.questionService.getQuestion(id);
        SiteUser author = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        this.commentService.create(question, null, commentForm.getContent(), author);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/answer/{id}")
    public String commentAnswer(@PathVariable("id") Integer id,
                                @Valid CommentForm commentForm, BindingResult bindingResult,
                                Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser author = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            return String.format("redirect:/question/detail/%s#answer_%s",
                    answer.getQuestion().getId(), answer.getId());
        }

        this.commentService.create(null, answer, commentForm.getContent(), author);
        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }
}