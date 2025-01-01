package com.mysite.sbb.question.controller;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.entity.AnswerForm;
import com.mysite.sbb.answer.service.AnswerService;
import com.mysite.sbb.category.entity.Category;
import com.mysite.sbb.category.service.CategoryService;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.comment.service.CommentService;
import com.mysite.sbb.global.util.Check;
import com.mysite.sbb.global.util.HttpMethod;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.entity.QuestionForm;
import com.mysite.sbb.question.service.QuestionService;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/{categoryName}")
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final CommentService commentService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String getList(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "kw", defaultValue = "") String kw,
                          @PathVariable("categoryName") String categoryName) {
        Category category = this.categoryService.findCategory(categoryName);
        Page<Question> paging = this.questionService.findAll(page, kw, category);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("category", category.getCategoryName());
        model.addAttribute("categoryList", this.categoryService.getList());
        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model, AnswerForm answerForm,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "sortOrder", defaultValue = "recommend") String sortOrder) {
        Question question = this.questionService.findQuestionById(id);
        List<Comment> commentList = this.commentService.findAll(id);

        Page<Answer> answerPage;
        if (sortOrder.equals("recommend")){
            answerPage = this.answerService.getAnswersByVotes(question, page);
        } else {
            answerPage = this.answerService.getAnswersByCreateDate(question, page);
        }

        model.addAttribute("question", question);
        model.addAttribute("answerPage", answerPage);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("commentList", commentList);
        model.addAttribute("categoryList", this.categoryService.getList());
        this.questionService.addViewCount(question);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(Model model, QuestionForm questionForm) {
        model.addAttribute("categoryList", this.categoryService.getList());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid QuestionForm questionForm, BindingResult bindingResult,
                         Principal principal, @PathVariable("categoryName") String categoryName, Model model) {
        if(bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.findUser(principal.getName());
        Category category = this.categoryService.findCategory(categoryName);
        Question question = this.questionService.createQuestion(questionForm.getSubject(), questionForm.getContent(),
                siteUser, category);
        model.addAttribute("categoryList", this.categoryService.getList());
        return "redirect:/question/detail/%s".formatted(question.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(@PathVariable("id") Integer questionId, Principal principal,
                         QuestionForm questionForm, Model model) {
        Question question = this.questionService.findQuestionById(questionId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(question, principal.getName(), HttpMethod.MODIFY);

        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        model.addAttribute("categoryList", this.categoryService.getList());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@PathVariable("id") Integer questionId, Principal principal,
                         @Valid QuestionForm questionForm, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.findQuestionById(questionId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(question, principal.getName(), HttpMethod.MODIFY);

        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        model.addAttribute("categoryList", this.categoryService.getList());
        return "redirect:/question/detail/%s".formatted(questionId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer questionId, Principal principal, Model model) {
        Question question = this.questionService.findQuestionById(questionId);

        // 메서드로 분리 : 수정 권한 확인 (질문 작성자와 동일한지 확인)
        Check.permission(question, principal.getName(), HttpMethod.DELETE);

        this.questionService.deleteQuestion(question);
        model.addAttribute("categoryList", this.categoryService.getList());
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(@PathVariable("id") Integer questionId, Principal principal, Model model,
                       @PathVariable("categoryName") String categoryName) {
        Question question = this.questionService.findQuestionById(questionId);
        SiteUser voteUser = this.userService.findUser(principal.getName());
        this.questionService.voteQuestion(question, voteUser);
        model.addAttribute("categoryList", this.categoryService.getList());
        return "redirect:/%s/detail/%s".formatted(categoryName, questionId);
    }


}
