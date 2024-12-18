package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {
    private final CommentService commentService;
    private final AnswerService answerService;
    private final QuestionService questionService;
    private final UserService userService;

    @PostMapping("/create/question/{id}")
    public String createQuestionComment(@PathVariable Integer id, Integer questionId, String content, Principal principal) {
        Question question = questionService.getQuestion(questionId);
        SiteUser siteUser = userService.getUser(principal.getName());

        commentService.createComment(content, question, siteUser);
        return "redirect:/question/detail/%s".formatted(id);
    }

    @PostMapping("/create/answer/{id}")
    public String createAnswerComment(@PathVariable Integer id, Integer answerId, String content, Principal principal) {
        Answer answer = answerService.getAnswer(answerId);
        SiteUser siteUser = userService.getUser(principal.getName());

        commentService.createComment(content, answer, siteUser);
        return "redirect:/question/detail/%s".formatted(id);
    }
}
