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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {


    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final CommentService commentService;

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
            model.addAttribute("answer", answer);
            return "question_detail";
        }

        this.commentService.create(answer, commentForm.getContent(), author);

        return String.format("redirect:/answer/detail/%s", answer.getId());
    }

}
