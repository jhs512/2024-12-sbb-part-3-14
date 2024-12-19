package com.mysite.sbb.answer;

import java.security.Principal;
import java.util.List;

import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.qustion.QuestionForm;
import com.mysite.sbb.qustion.QuestionRepository;
import com.mysite.sbb.qustion.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/answer")
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getSiteUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("content", question);
            return "qustion_detail";
        }
        answerService.create(siteUser, question, answerForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }
    @PostMapping("/modifyset/{id}")
    public String modifyAnswer(Model model,@PathVariable("id") Integer id,@Valid AnswerForm answerForm, BindingResult bindingResult) {
        Answer answer = answerService.getAnswer(id);
        Question question = answerService.getQestion(id);
        model.addAttribute("answer",question);
        if (bindingResult.hasErrors()) {
            return String.format("redirect:/question/detail/%s", question.getId());
        }
        this.answerService.modify(id, answerForm.getContent());
        return String.format("redirect:/question/detail/%s#answer_%s", question.getId(),id);
    }
    @GetMapping("/modify/{id}")
    public String modifyViewQuestion(Model model,@PathVariable("id") Integer id,AnswerForm answerForm){
        model.addAttribute("content",answerService.getAnswer(id));
        return "answer_modify";
    }
    @GetMapping("/delete/{id}")
    public String deleteAnswer(Model model,@PathVariable("id") Integer id,AnswerForm answerForm) {
        Question question = answerService.getQestion(id);
        model.addAttribute("content",question);
        this.answerService.delete(id);
        return String.format("redirect:/question/detail/%s", question.getId());
    }

    @GetMapping("/lists")
    public String createAnswer(Model model) {
        List<Answer> answers = this.answerService.getList();
        model.addAttribute("answerList", answers);
        return "answer_list";
    }
    @PostMapping("/comment_create/{id}")
    public String createComment(Model model,@PathVariable("id") Integer id,@Valid AnswerForm answerForm, BindingResult bindingResult,Principal principal) {
        Answer answer = answerService.getAnswer(id);
        Question question = answerService.getQestion(id);
        SiteUser user = userService.getSiteUser(principal.getName());
        answerService.create(user,question,answer, answerForm.getContent());
        List<Answer> answers = this.answerService.getList();
        model.addAttribute("answerList", answers);
        return String.format("redirect:/question/detail/%s", question.getId());
    }
    @GetMapping("recommend/{id}")
    public String recommned(Model model,@PathVariable("id") Integer id,Principal principal){
        Answer answer = answerService.getAnswer(id);
        SiteUser user = userService.getSiteUser(principal.getName());
        Question question = answerService.getQestion(id);
        answerService.vote(answer,user);
        model.addAttribute("content",question);
        return String.format("redirect:/question/detail/%s", question.getId());
    }



}
