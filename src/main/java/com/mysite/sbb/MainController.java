package com.mysite.sbb;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.service.AnswerService;
import com.mysite.sbb.category.entity.CategorysListDTO;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.comment.service.CommentService;
import com.mysite.sbb.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    @GetMapping("/")
    public String home(Model model) {
        CategorysListDTO categorysList = this.questionService.findAllRecentPosts();
        List<Answer> recentAnswers = this.answerService.findRecentAnswers();
        List<Comment> recentComments = this.commentService.findRecentComments();

        model.addAttribute("categorysList", categorysList);
        model.addAttribute("recentAnswers", recentAnswers);
        model.addAttribute("recentComments", recentComments);
        return "main_page";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello!";
    }
}
