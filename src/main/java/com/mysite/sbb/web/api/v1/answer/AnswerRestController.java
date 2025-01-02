package com.mysite.sbb.web.api.v1.answer;

import com.mysite.sbb.domain.answer.domain.Answer;
import com.mysite.sbb.domain.answer.service.AnswerServiceImpl;
import com.mysite.sbb.domain.question.domain.Question;
import com.mysite.sbb.domain.question.service.QuestionServiceImpl;
import com.mysite.sbb.domain.user.domain.SiteUser;
import com.mysite.sbb.domain.user.service.UserServiceImpl;
import com.mysite.sbb.web.api.v1.answer.dto.request.AnswerRequestDTO;
import com.mysite.sbb.web.api.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.mysite.sbb.global.util.CommonUtil.getUserName;
import static com.mysite.sbb.global.util.CommonUtil.validateUserPermission;

@Tag(name = "Answer Controller", description = "답변 컨트롤러")
@RestController
@RequestMapping("/api/v1/answer")
@RequiredArgsConstructor
@Slf4j
public class AnswerRestController {

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
            return ResponseEntity.badRequest().body(new ApiResponse(false, "답변 형식에 맞지 않습니다."));
        }

        try {
            Question question = questionService.getQuestion(questionId);
            SiteUser siteUser = userService.getUser(getUserName(principal));
            Answer answer = answerService.create(question, answerRequestDTO.getContent(), siteUser);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("questionId", answer.getQuestion().getId());
            responseData.put("answerId", answer.getId());

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "답변이 수정되었습니다.",
                    responseData
            ));
        } catch (Exception e) {
            log.error("Error updating answer: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "답변 생성 중 오류가 발생했습니다."));
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
            return ResponseEntity.badRequest().body(new ApiResponse(false, "올바른 형식이 아닙니다"));
        }

        try {
            Answer answer = answerService.getAnswer(id);
            validateUserPermission(getUserName(principal), answer.getAuthor().getUsername(), "수정권한");
            answerService.modify(answer, answerRequestDTO.getContent());
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "답변이 수정되었습니다.",
                    Map.of(
                            "questionId", answer.getQuestion().getId(),
                            "answerId", answer.getId()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "답변 수정 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAnswer(
            @PathVariable Integer id,
            Principal principal) {

        try {
            Answer answer = answerService.getAnswer(id);

            if (!answer.getAuthor().getUsername().equals(getUserName(principal))) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("삭제권한이 없습니다.");
            }

            Integer questionId = answer.getQuestion().getId();
            answerService.delete(answer);

            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, String.format("/question/detail/%s", questionId))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("답변 삭제 중 오류가 발생했습니다.");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote/{id}")
    public ResponseEntity<?> addVoteToAnswer(@PathVariable Integer id,Principal principal) {

        try {
            Answer answer = answerService.getAnswer(id);
            SiteUser siteUser = userService.getUser(getUserName(principal));

            answerService.vote(answer, siteUser);

            return ResponseEntity.status(HttpStatus.SEE_OTHER)
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