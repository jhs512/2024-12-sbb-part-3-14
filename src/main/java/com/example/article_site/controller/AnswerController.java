package com.example.article_site.controller;

import com.example.article_site.domain.Answer;
import com.example.article_site.domain.Author;
import com.example.article_site.domain.Question;
import com.example.article_site.dto.AnswerDetailDto;
import com.example.article_site.dto.AnswerListDto;
import com.example.article_site.dto.CommentDto;
import com.example.article_site.dto.QuestionDetailDto;
import com.example.article_site.form.AnswerForm;
import com.example.article_site.form.CommentForm;
import com.example.article_site.service.AnswerService;
import com.example.article_site.service.AuthorService;
import com.example.article_site.service.QuestionService;
import jakarta.servlet.http.HttpSession;
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

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final AuthorService authorService;
    private final SortPreference sortPreference;

    private static final String NO_MODIFY_PERMISSION = "수정 권한이 없습니다.";
    private static final String NO_DELETE_PERMISSION = "삭제 권한이 없습니다.";
    
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value="page", defaultValue="0") int page) {
        Page<AnswerListDto> paging = answerService.getAnswerDtoPage(page);
        model.addAttribute("paging", paging);
        return "answer_list";
    }

    @PreAuthorize(" isAuthenticated()")
    @PostMapping("/create/{id}")
    public String create(Model model,
                         @PathVariable("id") Long id,
                         @Valid AnswerForm answerForm,
                         BindingResult bindingResult,
                         @RequestParam(value= "page", defaultValue="0") int answerPage,
                         @RequestParam(required = false) String sort,
                         HttpSession session,
                         Principal principal) {
        if(bindingResult.hasErrors()) {
            return handleCreateValidationError(model, id, answerPage, sort, session);
        }

        Question question = questionService.getQuestionById(id);
        Author author = authorService.findByUsername(principal.getName());
        Answer answer = answerService.create(question, answerForm.getContent(), author);
        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }

    private String handleCreateValidationError(Model model, Long id, int answerPage, String sort, HttpSession session) {
        String currentSort = sortPreference.getCurrentSort(session, sort);
        QuestionDetailDto questionDetailDto = questionService.getQuestionDetailDto(id, answerPage, currentSort);
        model.addAttribute("question", questionDetailDto);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm,
                               @PathVariable("id") Long id,
                               Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        validateAuthorPermission(answer, principal.getName(), NO_MODIFY_PERMISSION);
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm,
                               BindingResult bindingResult,
                               @PathVariable("id") Long id,
                               Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = answerService.getAnswer(id);
        validateAuthorPermission(answer, principal.getName(), NO_MODIFY_PERMISSION);
        answerService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal,
                               @PathVariable("id") Long id) {
        Answer answer = answerService.getAnswer(id);
        validateAuthorPermission(answer, principal.getName(), NO_DELETE_PERMISSION);
        answerService.delete(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteAnswer(Principal principal,
                             @PathVariable("id") Long id){
        Answer answer = answerService.getAnswer(id);
        Author author = authorService.findByUsername(principal.getName());
        answerService.vote(answer, author);
        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }

    @GetMapping("/comment/{id}")
    public String comment(@PathVariable("id") Long id,
                         Model model){
        addAnswerDetailToModel(id, model);
        model.addAttribute("commentForm", new CommentForm());
        return "answer_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/{id}")
    public String comment(@PathVariable("id") Long id,
                          @Valid CommentForm commentForm,
                          BindingResult bindingResult,
                          Principal principal,
                          Model model) {
        if (bindingResult.hasErrors()) {
            addAnswerDetailToModel(id, model);
            return "answer_detail";
        }

        answerService.addComment(id, commentForm.getContent(), principal.getName());
        return "redirect:/answer/comment/{id}";
    }

    private void addAnswerDetailToModel(Long id, Model model) {
        AnswerDetailDto answerDetailDto = answerService.getAnswerDetailDto(id);
        model.addAttribute("answer", answerDetailDto);
    }

    private void validateAuthorPermission(Answer answer, String username, String errorMessage){
        if (!answer.getAuthor().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }
}
