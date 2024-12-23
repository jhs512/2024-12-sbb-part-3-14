package com.mysite.sbb.controller.api;

import com.mysite.sbb.domain.answer.dto.AnswerRequestDTO;
import com.mysite.sbb.domain.answer.entity.Answer;
import com.mysite.sbb.domain.question.entity.Question;
import com.mysite.sbb.domain.user.entity.SiteUser;
import com.mysite.sbb.service.impl.AnswerServiceImpl;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import com.mysite.sbb.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/answer")
@RequiredArgsConstructor
public class AnswerApiController {

    private final QuestionServiceImpl questionService;
    private final AnswerServiceImpl answerService;
    private final UserServiceImpl userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/questions/{questionId}")
    public ResponseEntity<?> createAnswer(
            @PathVariable Integer questionId,
            @Valid @RequestBody AnswerRequestDTO answerRequestDTO,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(bindingResult.getAllErrors());
        }

        try {
            Question question = questionService.getQuestion(questionId);
            SiteUser siteUser = userService.getUser(principal.getName());
            Answer answer = answerService.create(question, answerRequestDTO.getContent(), siteUser);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header(HttpHeaders.LOCATION,
                            String.format("/question/detail/%s#answer_%s",
                                    answer.getQuestion().getId(), answer.getId()))
                    .build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("답변 생성 중 오류가 발생했습니다.");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnswer(
            @PathVariable Integer id,
            @Valid @RequestBody AnswerRequestDTO answerRequestDTO,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(bindingResult.getAllErrors());
        }

        try {
            Answer answer = answerService.getAnswer(id);

            if (!answer.getAuthor().getUsername().equals(principal.getName())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("수정권한이 없습니다.");
            }

            answerService.modify(answer, answerRequestDTO.getContent());

            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION,
                            String.format("/question/detail/%s#answer_%s",
                                    answer.getQuestion().getId(), answer.getId()))
                    .build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("답변 수정 중 오류가 발생했습니다.");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAnswer(
            @PathVariable Integer id,
            Principal principal) {

        try {
            Answer answer = answerService.getAnswer(id);

            if (!answer.getAuthor().getUsername().equals(principal.getName())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("삭제권한이 없습니다.");
            }

            Integer questionId = answer.getQuestion().getId();
            answerService.delete(answer);

            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION,
                            String.format("/question/detail/%s", questionId))
                    .build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("답변 삭제 중 오류가 발생했습니다.");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/vote")
    public ResponseEntity<?> addVoteToAnswer(
            @PathVariable Integer id,
            Principal principal) {

        try {
            Answer answer = answerService.getAnswer(id);
            SiteUser siteUser = userService.getUser(principal.getName());

            answerService.vote(answer, siteUser);

            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION,
                            String.format("/question/detail/%s#answer_%s",
                                    answer.getQuestion().getId(), answer.getId()))
                    .build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("답변 추천 중 오류가 발생했습니다.");
        }
    }
}