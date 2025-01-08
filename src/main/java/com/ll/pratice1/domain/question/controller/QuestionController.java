package com.ll.pratice1.domain.question.controller;


import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.answer.AnswerForm;
import com.ll.pratice1.domain.answer.service.AnswerService;
import com.ll.pratice1.domain.category.Category;
import com.ll.pratice1.domain.category.service.CategoryService;
import com.ll.pratice1.domain.comment.CommentForm;
import com.ll.pratice1.domain.comment.service.CommentService;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.question.QuestionForm;
import com.ll.pratice1.domain.question.service.QuestionService;
import com.ll.pratice1.domain.user.SiteUser;
import com.ll.pratice1.domain.user.service.UserService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final AnswerService answerService;
    private final CommentService commentService;


    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0")int page,
                       @RequestParam(value = "kw", defaultValue = "")String kw,
                       @RequestParam(value = "category", defaultValue = "")String category,
                       @RequestParam(name = "sort", required = false, defaultValue = "") String sort){
        Page<Question> paging = this.questionService.getList(page, kw, category, sort);
        List<Category> categoryList = this.categoryService.getCategoryAll();
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("paging", paging);
        model.addAttribute("sort", sort);
        model.addAttribute("kw", kw);
        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @RequestParam(value = "page", defaultValue = "0")int page,
                         @PathVariable("id") Integer id, @RequestParam(defaultValue = "latest") String sort,
                         CommentForm commentForm, AnswerForm answerForm){
        Question question = this.questionService.getQuestionAndViewCount(id);
        Page<Answer> paging = this.answerService.getAnswerList(question, page, sort);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", paging);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(Model model, QuestionForm questionForm){
        List<Category> categoryList = this.categoryService.getCategoryAll();
        model.addAttribute("categoryList",categoryList);
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Category category = this.categoryService.getCategory(questionForm.getCategory());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), category, siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm){
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, @PathVariable("id") Integer id,
                                 BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id){
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id){
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", question.getId());
    }



}
