package com.mysite.sbb.question.controller;

import com.mysite.sbb.answer.form.AnswerForm;
import com.mysite.sbb.answer.service.AnswerService;
import com.mysite.sbb.category.repository.CategoryRepository;
import com.mysite.sbb.entity.Answer;
import com.mysite.sbb.entity.Category;
import com.mysite.sbb.entity.Question;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.question.form.QuestionForm;
import com.mysite.sbb.question.service.QuestionService;
import com.mysite.sbb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/list")
    public String list(Model model ,@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = this.questionService.getList(page,kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("recentAnswers", questionService.getRecentAnswers());
        model.addAttribute("recentComments", questionService.getRecentComments());
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         @RequestParam(value = "page", defaultValue = "0") int page){
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        Page<Answer> paging = this.answerService.getListByVoterCount(question.getId(),page); // 댓글에 Paging 및 추천 수 별 정렬 기능
        model.addAttribute("answerForm", new AnswerForm());
        model.addAttribute("paging", paging); // 댓글에 Paging 및 추천 수 별 정렬 기능
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm, Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(
            @Valid QuestionForm questionForm,
            BindingResult bindingResult,
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "isExistingCategory", defaultValue = "false") boolean isExistingCategory,
            Principal principal,
            Model model
    ) {
        System.out.println("카테고리 전송 값: " + categoryName);
        System.out.println("기존 카테고리 여부: " + isExistingCategory);

        if (bindingResult.hasErrors()) {
            List<Category> categories = this.categoryRepository.findAll();
            model.addAttribute("categories", categories);
            return "question_form";  // 질문 등록 폼으로 리턴
        }

        try {
            SiteUser siteUser = this.userService.getUser(principal.getName());

            this.questionService.create(
                    questionForm.getSubject(),
                    questionForm.getContent(),
                    categoryName,
                    isExistingCategory,
                    siteUser
            );

            return "redirect:/question/list";  // 정상 등록 후 리스트 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            List<Category> categories = this.categoryRepository.findAll();
            model.addAttribute("categories", categories);
            return "question_form";  // 에러 시 다시 폼 리턴
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한 없음");
        }

        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());

        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
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
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권한이 없습니다.");
        }

        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question,siteUser);

        return String.format("redirect:/question/detail/%s", id);
    }

}

