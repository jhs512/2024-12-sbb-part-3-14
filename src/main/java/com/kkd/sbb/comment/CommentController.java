package com.kkd.sbb.comment;

import com.kkd.sbb.answer.Answer;
import com.kkd.sbb.answer.AnswerForm;
import com.kkd.sbb.answer.AnswerService;
import com.kkd.sbb.question.Question;
import com.kkd.sbb.question.QuestionService;
import com.kkd.sbb.user.SiteUser;
import com.kkd.sbb.user.UserService;
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
                                  @Valid CommentForm commentForm,
                                  BindingResult bindingResult,
                                  AnswerForm answerForm,
                                  Principal principal,
                                  @RequestParam(value = "ans-page", defaultValue = "0") int answerPage,
                                  @RequestParam(value = "ans-ordering", defaultValue = "time") String answerOrderMethod) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            Page<Answer> answerPaging = this.answerService.getAnswerList(question, answerPage, answerOrderMethod);
            List<Comment> commentList = this.commentService.getCommentList(question);
            model.addAttribute("question", question);
            model.addAttribute("ans_paging",answerPaging);
            model.addAttribute("comment_list", commentList);
            return "question_detail";
        }
        this.commentService.create(commentForm.getContent(), question, null, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String answerComment(Model model, @PathVariable Integer id,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                AnswerForm answerForm,
                                Principal principal,
                                @RequestParam(value = "ans-page", defaultValue = "0") int answerPage,
                                @RequestParam(value = "ans-ordering", defaultValue = "time") String answerOrderMethod) {
        Answer answer = this.answerService.getAnswer(id);
        Question question = answer.getQuestion();
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            Page<Answer> answerPaging = this.answerService.getAnswerList(question, answerPage, answerOrderMethod);
            List<Comment> commentList = this.commentService.getCommentList(question);
            model.addAttribute("question", question);
            model.addAttribute("ans_paging",answerPaging);
            model.addAttribute("comment_list", commentList);
            return "question_detail";
        }
        this.commentService.create(commentForm.getContent(), question, answer, siteUser);
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
