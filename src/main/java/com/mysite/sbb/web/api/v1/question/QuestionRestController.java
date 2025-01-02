package com.mysite.sbb.web.api.v1.question;

import com.mysite.sbb.web.api.common.ApiResponse;
import com.mysite.sbb.web.api.v1.question.dto.request.QuestionRequestDTO;
import com.mysite.sbb.domain.question.service.QuestionServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.mysite.sbb.global.util.CommonUtil.getUserName;

@Tag(name = "Question Controller", description = "질문 컨트롤러")
@RequestMapping("/api/v1/question")
@RequiredArgsConstructor
@RestController
public class QuestionRestController {

    private final QuestionServiceImpl questionServiceImpl;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ApiResponse> createNewQuestion(
            @Valid @RequestBody QuestionRequestDTO questionRequestDTO,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "입력값이 올바르지 않습니다."));
        }
        questionServiceImpl.create(questionRequestDTO, getUserName(principal));
        return ResponseEntity.ok(new ApiResponse(true, "게시물 작성이 완료되었습니다."));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateQuestion(
            @Valid @RequestBody QuestionRequestDTO questionRequestDTO,
            BindingResult bindingResult,
            Principal principal,
            @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "입력값이 올바르지 않습니다."));
        }
        try {
            questionServiceImpl.modify(id, questionRequestDTO, getUserName(principal));
            return ResponseEntity.ok(new ApiResponse(true, "질문이 수정되었습니다.", id));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "질문 수정에 실패했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeQuestion(
            @PathVariable("id") Integer id,
            Principal principal) {
        try {
            this.questionServiceImpl.delete(id, getUserName(principal));
            return ResponseEntity.ok(new ApiResponse(true, "질문이 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "질문 삭제에 실패했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/vote")
    public ResponseEntity<?> addVoteToQuestion(@PathVariable("id") Integer id, Principal principal) {
        this.questionServiceImpl.vote(id, getUserName(principal));
        return ResponseEntity.ok(new ApiResponse(true, "추천이 완료되었습니다."));
    }

}
