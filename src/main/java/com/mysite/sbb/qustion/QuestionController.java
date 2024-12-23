package com.mysite.sbb.qustion;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.catrgory.Category;
import com.mysite.sbb.catrgory.CategoryService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
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

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/list/{id}")
    public String list(Model model, @PathVariable("id") Integer id, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {

            Category category = categoryService.getCategory(id);
            if(category != null) {
                Page<Question> paging = this.questionService.getList(page, kw, category);
                model.addAttribute("paging", paging);
                model.addAttribute("kw", kw);
                model.addAttribute("category", category);
                model.addAttribute("skip", 0);
            }
            else
                model.addAttribute("skip", 1);
        return "/question/question_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/insert/{id}")
    public String insertQuestion(Model model, QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        if (principal == null) {
            return "redirect:/question/list/" + id;
        }
        Category category = categoryService.getCategory(id);
        model.addAttribute("category", category);
        return "/question/question_insert";
    }

    @PostMapping("/create/{id}")
    public String createQuestion(@PathVariable("id") Integer id, @Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        SiteUser siteUser = this.userService.getSiteUser(principal.getName());
        Category category = this.categoryService.getCategory(id);
        if (bindingResult.hasErrors()) {
            return "/question/question_insert";
        }
        this.questionService.create(siteUser, questionForm.getSubject(), questionForm.getContent(), category);
        return "redirect:/question/list/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(Model model, @PathVariable("id") Integer id) {
        this.questionService.delete(id);
        return "redirect:/question/list";
    }

    @PostMapping("/modifyset/{id}")
    public String modifyQuestion(Model model, @PathVariable("id") Integer id, @Valid QuestionForm questionForm, BindingResult bindingResult) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("content", question);
        if (bindingResult.hasErrors()) {
            return String.format("redirect:/question/detail/%s", id);
        }
        this.questionService.modify(id, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @GetMapping("/modify/{id}")
    public String modifyViewQuestion(Model model, @PathVariable("id") Integer id, QuestionForm questionForm) {
        model.addAttribute("content", questionService.getQuestion(id));
        return "/question/question_modify";
    }


    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, @RequestParam(value = "page", defaultValue = "0") int page, AnswerForm answerForm) {
        Question question = questionService.getQuestion(id);
        Page<Answer> answerList = answerService.getList(question, page);
        List<Answer> answerBest = this.questionService.best(id);
        model.addAttribute("paging", answerList);
        model.addAttribute("content", question);
        model.addAttribute("best", answerBest);
        return "/question/qustion_detail";
    }

    @GetMapping("recommend/{id}")
    public String recommend(Model model, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser user = this.userService.getSiteUser(principal.getName());
        questionService.vote(question, user);
        model.addAttribute("content", question);
        return String.format("redirect:/question/detail/%s", id);
    }


}
