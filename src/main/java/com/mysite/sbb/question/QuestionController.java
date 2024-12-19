package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
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

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")
public class QuestionController {

    private final AnswerService answerService;
    private final QuestionService questionService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value="page", defaultValue = "0") int page,
                       @RequestParam(value="kw", defaultValue = "") String kw,
                       @RequestParam(value="category", defaultValue= "1") int categoryId) {
        Page<Question> paging = this.questionService.getList(page, kw, categoryId);
        List<Category> categoryList = categoryService.getCategoryList();
        Category category = categoryService.getCategory(categoryId);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("category", category);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         AnswerForm answerForm,
                         @RequestParam(value="page", defaultValue="0")int page) {
        Question question = this.questionService.getQuestion(id);
        Page<Answer> paging = this.answerService.getList(id, page);
        model.addAttribute("paging", paging);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(
            Model model,
            QuestionForm questionForm) {
        model.addAttribute("categoryList", categoryService.getCategoryList());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate( Model model,
                                  @Valid QuestionForm questionForm,
                                  BindingResult bindingResult,
                                  Principal principcal ){

        SiteUser siteUser = userService.getUser(principcal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryList", categoryService.getCategoryList());
            return "question_form";
        }

        Category category = categoryService.getCategory(questionForm.getCategoryId());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, category);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify( Model model,
                                  QuestionForm questionForm,
                                 @PathVariable("id") Integer id,
                                 Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        questionForm.setCategoryId(question.getCategory().getId());
        model.addAttribute("categoryList", categoryService.getCategoryList());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 @PathVariable("id") Integer id){

        if(bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        Category category = categoryService.getCategory(questionForm.getCategoryId());
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent(), category);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal,
                                 @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal,
                               @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-question")
    public String myQuestion(Model model,
                             @RequestParam(value="page", defaultValue = "0") int page,
                             Principal principal) {
        String username = principal.getName();
        Page<Question> paging = questionService.getMyQuestionList(username, page);
        model.addAttribute("paging", paging);
        return "my_question";
    }
}
