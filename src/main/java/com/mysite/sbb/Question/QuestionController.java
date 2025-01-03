package com.mysite.sbb.Question;

import com.mysite.sbb.Answer.AnswerForm;
import com.mysite.sbb.Answer.AnswerService;
import com.mysite.sbb.Category.CategoryService;
import com.mysite.sbb.Comment.CommentForm;
import com.mysite.sbb.Comment.CommentService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Map;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final AnswerService answerService;
    private final CommentService commentService;

    // 질문 목록 보기 (카테고리 필터 추가)
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "category", required = false) Integer categoryId) {
        // 기존 검색 결과 데이터
        Map<String, Object> results = questionService.searchAll(kw, page, categoryId);
        model.addAttribute("questions", results.get("questions"));
        model.addAttribute("answers", results.get("answers"));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("kw", kw);

        // 최근 답변과 최근 댓글 추가
        model.addAttribute("recentAnswers", answerService.getRecentAnswers(5));  // 최근 답변 5개
        model.addAttribute("recentComments", commentService.getRecentComments(5));  // 최근 댓글 5개

        return "question_list";
    }
    // 질문 상세 보기
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        // 조회 수 증가
        this.questionService.incrementViewCount(id);

        // 질문 데이터 가져오기
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        model.addAttribute("commentForm", new CommentForm()); // 댓글 폼 객체 추가
        return "question_detail";
    }

    // 질문 작성 폼 보기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm, Model model) {
        model.addAttribute("categories", categoryService.findAll()); // 카테고리 목록 추가
        return "question_form";
    }

    // 질문 작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(
                questionForm.getSubject(),
                questionForm.getContent(),
                questionForm.getCategoryId(), // 선택된 카테고리 ID
                siteUser
        );
        return "redirect:/question/list";
    }

    // 질문 수정 폼 보기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal, Model model) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        questionForm.setCategoryId(question.getCategory().getId()); // 기존 카테고리 설정
        model.addAttribute("categories", categoryService.findAll()); // 카테고리 목록 추가
        return "question_form";
    }

    // 질문 수정
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
                                 @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent(), questionForm.getCategoryId());
        return String.format("redirect:/question/detail/%s", id);
    }

    // 질문 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    // 질문 추천
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }


}

