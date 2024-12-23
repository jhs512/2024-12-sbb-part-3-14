package com.mysite.sbb.controller.view;

import com.mysite.sbb.domain.dto.AnswerRequestDTO;
import com.mysite.sbb.domain.dto.QuestionDetailResponseDTO;
import com.mysite.sbb.domain.dto.QuestionRequestDTO;
import com.mysite.sbb.domain.dto.UserRequestDTO;
import com.mysite.sbb.domain.entity.Answer;
import com.mysite.sbb.domain.entity.Question;
import com.mysite.sbb.service.impl.AnswerServiceImpl;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import com.mysite.sbb.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final QuestionServiceImpl questionService;
    private final AnswerServiceImpl answerService;
    private final UserServiceImpl userService;

    private static final String QUESTION_LIST_VIEW = "question_list";
    private static final String QUESTION_DETAIL_VIEW = "question_detail";
    private static final String QUESTION_FORM_VIEW = "question_form";
    private static final String ANSWER_FORM_VIEW = "answer_form";
    private static final String SIGNUP_FORM_VIEW = "signup_form";
    private static final String LOGIN_FORM_VIEW = "login_form";

    @GetMapping("/")
    public String root() {
        return "redirect:/question/list";
    }

    @GetMapping("/question/list")
    public String showQuestionList(Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "kw", defaultValue = "") String kw) {
        model.addAttribute("paging", questionService.getList(page, kw));
        model.addAttribute("kw", kw);
        return QUESTION_LIST_VIEW;
    }

    @GetMapping(value = "/question/detail/{id}")
    public String showQuestionDetail(Model model,
                                     @PathVariable("id") Integer id,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "sortKeyword", defaultValue = "createDate") String sortKeyword) {

        QuestionDetailResponseDTO question = this.questionService.getQuestionDetail(id, page, sortKeyword);
        model.addAttribute("question", question);
        model.addAttribute("sort", sortKeyword); // 선택된 정렬 기준 전달
        model.addAttribute("answerRequestDTO", new AnswerRequestDTO()); // Form 초기화
        return QUESTION_DETAIL_VIEW;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create")
    public String showQuestionForm(@ModelAttribute QuestionRequestDTO questionRequestDTO) {
        return QUESTION_FORM_VIEW;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/modify/{id}")
    public String showQuestionModifyForm(QuestionRequestDTO questionRequestDTO, @PathVariable("id") Integer id, Principal principal) {
        Question question = questionService.getQuestion(id);
        validateAuth(principal.getName(), question.getAuthor().getUsername());
        questionRequestDTO.setSubject(question.getSubject());
        questionRequestDTO.setContent(question.getContent());
        return QUESTION_FORM_VIEW;
    }

    // 답변 수정 페이지 요청
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/answer/modify/{id}")
    public String showAnswerModifyForm(AnswerRequestDTO answerRequestDTO, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        validateAuth(principal.getName(), answer.getAuthor().getUsername());
        answerRequestDTO.setContent(answer.getContent());
        return ANSWER_FORM_VIEW;
    }

    // 사용자 관련 뷰
    @GetMapping("/user/signup")
    public String showSignupForm(UserRequestDTO userRequestDTO) {
        return SIGNUP_FORM_VIEW;
    }

    @GetMapping("/user/login")
    public String showLoginForm() {
        return LOGIN_FORM_VIEW;
    }

    private void validateAuth(String currentUsername, String authorUsername) {
        if (!currentUsername.equals(authorUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
    }
}
