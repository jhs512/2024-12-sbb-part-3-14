package com.mysite.sbb.qustion;

import com.mysite.sbb.answer.AnswerForm;
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
    private final UserService userService;
    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page){
        Page<Question> paging = questionService.getList(page);
        model.addAttribute("paging",paging);
        return "question_list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/insert")
    public String insertQuestion(QuestionForm questionForm,Principal principal){
        if(principal == null) {
            return "redirect:/question/list";
        }

        return "question_insert";
    }
    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        SiteUser siteUser = this.userService.getSiteUser(principal.getName());
        if (bindingResult.hasErrors()) {
            return "question_insert";
        }
        this.questionService.create(siteUser ,questionForm.getSubject(), questionForm.getContent());
        return "redirect:/question/list";
    }
    @GetMapping("/delete/{id}")
    public String deleteQuestion(Model model,@PathVariable("id") Integer id) {
        this.questionService.delete(id);
        return "redirect:/question/list";
    }
    @PostMapping("/modifyset/{id}")
    public String modifyQuestion(Model model,@PathVariable("id") Integer id,@Valid QuestionForm questionForm, BindingResult bindingResult) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("content",question);
        if (bindingResult.hasErrors()) {
            return String.format("redirect:/question/detail/%s", id);
        }
        this.questionService.modify(id,questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }
    @GetMapping("/modify/{id}")
    public String modifyViewQuestion(Model model,@PathVariable("id") Integer id,QuestionForm questionForm){
        model.addAttribute("content",questionService.getQuestion(id));
        return "question_modify";
    }


    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id,AnswerForm answerForm){
        model.addAttribute("content",questionService.getQuestion(id));
        return "qustion_detail";
    }
    @GetMapping("recommend/{id}")
    public String recommend(Model model,@PathVariable("id") Integer id,Principal principal){
        Question question =  this.questionService.getQuestion(id);
        SiteUser user = this.userService.getSiteUser(principal.getName());
        questionService.vote(question,user);
        model.addAttribute("content",question);
        return String.format("redirect:/question/detail/%s", id);
    }


}
