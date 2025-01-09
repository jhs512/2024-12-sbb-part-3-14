package com.mysite.sbb.question.controller;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.entity.form.AnswerForm;
import com.mysite.sbb.answer.service.AnswerService;
import com.mysite.sbb.category.entity.Category;
import com.mysite.sbb.category.service.CategoryService;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.comment.entity.form.CommentForm;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.entity.form.QuestionForm;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.question.service.QuestionService;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw, @RequestParam(value = "filter", defaultValue = "") String filter) {
        Page<Question> paging = questionService.getList(page, kw, filter);

        model.addAttribute("filter", filter);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question/question_list";
    }

    @GetMapping("/detail/{id}")
    @Transactional
    public String detail(@PathVariable long id, @RequestParam(value = "page", defaultValue = "0") int page, Model model, AnswerForm answerForm, CommentForm commentForm) {
        Page<Answer> paging = answerService.findByQuestionId(id, page);
        Question question = questionService.findById(id);
        question.setVisited(question.getVisited()+1);

        List<Comment> questionComment = new ArrayList<>();
        List<Comment> answerComment = new ArrayList<>();
        List<Comment> comments = question.getComments();

        if (comments != null && !comments.isEmpty()) {
            for (Comment comment : comments) {
                if (comment.getQuestion() != null && comment.getAnswer() == null) {
                    questionComment.add(comment);
                } else {
                    answerComment.add(comment);
                }
            }
        }

        model.addAttribute("question", question);
        model.addAttribute("paging", paging);
        model.addAttribute("questionComment", questionComment);
        System.out.println("answerComment = " + answerComment);
        model.addAttribute("answerComment", answerComment);
        System.out.println("detail===");
        return "question/question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm, Model model) {
        List<Category> categories = categoryService.findAll();

        model.addAttribute("categories", categories);
        return "form/question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "form/question_form";
        }
        SiteUser siteUser = userService.findByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        questionService.write(questionForm.getSubject(), questionForm.getContent(), questionForm.getCategory(), siteUser);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(QuestionForm questionForm, @PathVariable long id, Principal principal) {
        Question question = questionService.findById(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        questionForm.setContent(question.getContent());
        questionForm.setSubject(question.getSubject());

        return "form/question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@Valid QuestionForm questionForm, BindingResult bindingResult, @PathVariable long id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "form/question_form";
        }
        Question question = questionService.findById(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return "redirect:/question/detail/%s".formatted(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable long id, Principal principal) {
        Question question = questionService.findById(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(@PathVariable long id, Principal principal) {
        Question question = questionService.findById(id);

        SiteUser siteUser = userService.findByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        questionService.vote(question, siteUser);

        return "redirect:/question/detail/%s".formatted(id);
    }
}
