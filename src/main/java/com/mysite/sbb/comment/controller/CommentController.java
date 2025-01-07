package com.mysite.sbb.comment.controller;

import com.mysite.sbb.answer.service.AnswerService;
import com.mysite.sbb.comment.service.CommentService;
import com.mysite.sbb.entity.Answer;
import com.mysite.sbb.entity.Comment;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final AnswerService answerService;
    private final UserService userService;

    //  최상위 댓글 생성
    @PostMapping("/create/{answerId}")
    public String createComment(@PathVariable("answerId") Integer answerId,
                                @RequestParam String content, Principal principal) {
        Answer answer = this.answerService.getAnswer(answerId);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.createComment(answer, siteUser, content);

        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answerId);
    }

    //  대댓글
    @PostMapping("/reply/{commentId}")
    public  String createReply(@PathVariable("commentId") Integer commentId,
                               @RequestParam String content, Principal principal) {

        Comment parent = this.commentService.getComment(commentId);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.createReply(parent, siteUser, content);

        return String.format("redirect:/question/detail/%s#comment_%s",
                parent.getAnswer().getQuestion().getId(), commentId);
    }
}
