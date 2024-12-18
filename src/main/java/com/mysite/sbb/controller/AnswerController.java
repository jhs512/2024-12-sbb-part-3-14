package com.mysite.sbb.controller;

import com.mysite.sbb.form.AnswerForm;
import com.mysite.sbb.domain.Answer;
import com.mysite.sbb.domain.Question;
import com.mysite.sbb.service.impl.AnswerServiceImpl;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import com.mysite.sbb.domain.SiteUser;
import com.mysite.sbb.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionServiceImpl questionServiceImpl; // 질문 데이터를 처리하는 서비스
    private final AnswerServiceImpl answerServiceImpl;     // 답변 데이터를 처리하는 서비스
    private final UserServiceImpl userServiceImpl;         // 사용자 데이터를 처리하는 서비스

    // 인증된 사용자만 접근 가능
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable Integer id,
                               @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        // 1. 질문 ID를 사용하여 질문 객체 조회
        Question question = questionServiceImpl.getQuestion(id);
        // 2. 현재 로그인된 사용자 정보 조회
        SiteUser siteUser = userServiceImpl.getUser(principal.getName());

        // 3. 답변 내용에 유효성 검증 오류가 있으면 다시 질문 상세 페이지로 이동
        if(bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        // 4. 답변 생성 및 저장
        Answer answer = this.answerServiceImpl.create(question, answerForm.getContent(), siteUser);

        // 5. 질문 상세 페이지로 리다이렉트하며 앵커를 사용해 새로 생성된 답변 위치로 이동
        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }

    // 답변 수정 페이지 요청
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        // 1. 수정할 답변 조회
        Answer answer = this.answerServiceImpl.getAnswer(id);
        // 2. 수정 권한 확인: 작성자가 아니면 예외 발생
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        // 3. 기존 답변 내용을 폼에 세팅
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    // 수정된 답변 저장
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal) {
        // 1. 유효성 검증 오류가 있으면 수정 폼으로 이동
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        // 2. 수정할 답변 조회 및 작성자 확인
        Answer answer = this.answerServiceImpl.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        // 3. 답변 수정
        this.answerServiceImpl.modify(answer, answerForm.getContent());
        // 4. 수정된 답변 위치로 이동
        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }

    // 답변 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        // 1. 삭제할 답변 조회 및 작성자 확인
        Answer answer = this.answerServiceImpl.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        // 2. 답변 삭제
        this.answerServiceImpl.delete(answer);
        // 3. 질문 상세 페이지로 리다이렉트
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    // 답변 추천(투표)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        // 1. 투표할 답변 조회
        Answer answer = this.answerServiceImpl.getAnswer(id);
        // 2. 로그인된 사용자 정보 조회
        try {
            SiteUser siteUser = this.userServiceImpl.getUser(principal.getName());
            this.answerServiceImpl.vote(answer, siteUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }
}