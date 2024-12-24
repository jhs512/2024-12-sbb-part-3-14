package com.site.sss.Comment.QuestionComment;

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

@RequestMapping("/questionComment")
@RequiredArgsConstructor
@Controller
public class QuestionCommentController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final QuestionCommentService questionCommentService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}")
    public String createComment(Model model, @PathVariable("id") Integer id
            , @Valid CommentForm commentForm, BindingResult bindingResult
            , Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        QuestionComment questionComment= questionCommentService.create(question,commentForm.getContent(),siteUser);

        return String.format("redirect:/question/detail/%s#questionComment_%s",
                id,questionComment.getId());


    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String questionCommentDelete(Principal principal, @PathVariable("id") Integer id) {
        QuestionComment questionComment = questionCommentService.getQuestionComment(id);
        if (!questionComment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionCommentService.delete(questionComment);
        return String.format("redirect:/question/detail/%s", questionComment.getQuestion().getId());
    }
}
