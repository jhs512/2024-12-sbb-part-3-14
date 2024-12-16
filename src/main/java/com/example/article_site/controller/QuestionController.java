package com.example.article_site.controller;

import com.example.article_site.domain.Author;
import com.example.article_site.domain.Question;
import com.example.article_site.dto.QuestionDetailDto;
import com.example.article_site.dto.QuestionListDto;
import com.example.article_site.form.AnswerForm;
import com.example.article_site.form.QuestionForm;
import com.example.article_site.service.AuthorService;
import com.example.article_site.service.CategoryService;
import com.example.article_site.service.QuestionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final AuthorService authorService;
    private final SortPreference sortPreference;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value="category", defaultValue = "전체") String category,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        addListDataToModel(model, page, category, kw);
        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id,
                         Model model,
                         @RequestParam(value= "page", defaultValue="0") int answerPage,
                         @RequestParam(required = false) String sort,
                         HttpSession session,
                         AnswerForm answerForm) {

        // Question views update
        questionService.updateViews(id);

        // Question info
        String currentSort = sortPreference.getCurrentSort(session, sort);
        QuestionDetailDto questionDetailDto =  questionService.getQuestionDetailDto(id, answerPage, currentSort);
        model.addAttribute("question", questionDetailDto);

        return "question_detail";
    }

    @PreAuthorize(" isAuthenticated()")
    @GetMapping("/create")
    public String create(QuestionForm questionForm,
                         Model model) {

        addCategoriesToModel(model);
        return "question_form";
    }

    @PreAuthorize(" isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 Model model) {
        if(bindingResult.hasErrors()) {
            addCategoriesToModel(model);
            return "question_form";
        }
        questionService.create(questionForm.getSubject(), questionForm.getContent(), questionForm.getCategory(), principal.getName());
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm,
                                 @PathVariable("id") Long id,
                                 Principal principal,
                                 Model model) {
        Question question = questionService.getQuestionById(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        addCategoriesToModel(model);
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 @PathVariable("id") Long id,
                                 Principal principal) {
        if(bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = questionService.getQuestionById(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionService.modify(question, questionForm.getSubject(), questionForm.getContent(), questionForm.getCategory());
        return "redirect:/question/detail/{id}" ;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal,
                                 @PathVariable("id") Long id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteQuestion(Principal principal,
                               @PathVariable("id") Long id){
        Question question = questionService.getQuestion(id);
        Author author = authorService.findByUsername(principal.getName());
        questionService.vote(question, author);
        return "redirect:/question/detail/{id}";
    }

    private void addListDataToModel(Model model, int page, String category, String kw) {
        // Category List
        List<String> categories = categoryService.getCategoryNames();
        model.addAttribute("categoryNames", categories);

        // Question List
        Page<QuestionListDto> paging = questionService.getQuestionDtoPage(page, kw, category);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("category", category);
    }

    private void addCategoriesToModel(Model model) {
        List<String> categories = categoryService.getCategoryNames();
        model.addAttribute("categories", categories);
    }
}
