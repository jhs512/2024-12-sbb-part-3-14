package com.mysite.sbb.controller.view;

import com.mysite.sbb.domain.dto.AnswerRequestDTO;
import com.mysite.sbb.domain.entity.Answer;
import com.mysite.sbb.domain.dto.QuestionDetailResponseDTO;
import com.mysite.sbb.domain.dto.QuestionRequestDTO;
import com.mysite.sbb.domain.entity.Question;
import com.mysite.sbb.domain.dto.UserRequestDTO;
import com.mysite.sbb.service.impl.AnswerServiceImpl;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import com.mysite.sbb.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        return "question_list";
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
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create")
    public String showQuestionForm(@ModelAttribute QuestionRequestDTO questionRequestDTO) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/modify/{id}")
    public String showQuestionModifyForm(QuestionRequestDTO questionRequestDTO, @PathVariable("id") Integer id, Principal principal) {

        Question question = questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        questionRequestDTO.setSubject(question.getSubject());
        questionRequestDTO.setContent(question.getContent());
        return "question_form";
    }

    // 답변 수정 페이지 요청
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/answer/modify/{id}")
    public String showAnswerModifyForm(AnswerRequestDTO answerRequestDTO, @PathVariable("id") Integer id, Principal principal) {
        // 1. 수정할 답변 조회
        Answer answer = answerService.getAnswer(id);

        // 2. 수정 권한 확인: 작성자가 아니면 예외 발생
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        // 3. 기존 답변 내용을 폼에 세팅
        answerRequestDTO.setContent(answer.getContent());
        return "answer_form";
    }

    // 사용자 관련 뷰
    @GetMapping("/users/signup")
    public String showSignupForm(UserRequestDTO userRequestDTO) {
        return "signup_form";
    }

    @GetMapping("/users/login")
    public String showLoginForm() {
        return "login_form";
    }
}
