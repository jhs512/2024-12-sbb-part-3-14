package com.site.sss.Comment.AnswerComment;

import com.site.sss.Comment.CommentForm;
import com.site.sss.answer.Answer;
import com.site.sss.answer.AnswerService;
import com.site.sss.question.Question;
import com.site.sss.question.QuestionService;
import com.site.sss.user.SiteUser;
import com.site.sss.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;


@RequestMapping("/answerComment")
@RequiredArgsConstructor
@Controller
public class AnswerCommentController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final AnswerCommentService answerCommentService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("{questionId}/{answerId}")
    public String createComment(Model model, @PathVariable("questionId") Integer qid, @PathVariable("answerId") Integer aid
            , @Valid CommentForm commentForm, BindingResult bindingResult
            , Principal principal) {
        Question question = this.questionService.getQuestion(qid);
        Answer answer =this.answerService.getAnswer(aid);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        AnswerComment answerComment= answerCommentService.create(answer,commentForm.getContent(),siteUser);

        return String.format("redirect:/question/detail/%s#answer_%s",
                qid, aid);

    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{questionId}/{answerId}")
    public String questionCommentDelete(Principal principal,
                                         @PathVariable("questionId") Integer qid,
                                         @PathVariable("answerId") Integer aid) {
        AnswerComment answerComment = answerCommentService.getAnswerComment(aid);
        if (!answerComment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.answerCommentService.delete(answerComment);
        return String.format("redirect:/question/detail/%s", qid);
    }
}
