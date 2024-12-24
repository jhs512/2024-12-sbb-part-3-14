package com.site.sss.Board;

import com.site.sss.Category.Category;
import com.site.sss.Category.CategoryNum;
import com.site.sss.Category.CategoryService;
import com.site.sss.Comment.AnswerComment.AnswerCommentService;
import com.site.sss.Comment.QuestionComment.QuestionCommentService;
import com.site.sss.answer.AnswerService;
import com.site.sss.question.Question;
import com.site.sss.question.QuestionForm;
import com.site.sss.question.QuestionService;
import com.site.sss.user.SiteUser;
import com.site.sss.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;


@RequestMapping("/learning")
@RequiredArgsConstructor
@Controller
public class LearningController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;
    private final QuestionCommentService questionCommentService;
    private final AnswerCommentService answerCommentService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page",defaultValue = "0") int page
            , @RequestParam(value="kw",defaultValue = "") String kw) {
        Category category =this.categoryService.getCategory(CategoryNum.LEARNING.getValue());
        Page<Question> paging = this.questionService.getList(page,kw,category);
        model.addAttribute("paging",paging);
        model.addAttribute("kw",kw);
        return "learning_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm
            , BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());
        Category category =this.categoryService.getCategory(CategoryNum.LEARNING.getValue());

        this.questionService.create(questionForm.getSubject(), questionForm.getContent(),siteUser,category);
        return "redirect:/learning/list";
    }








}
