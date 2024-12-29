package com.mysite.sbb.web.api.common.v1.comment;

import com.mysite.sbb.domain.comment.Comment;
import com.mysite.sbb.domain.comment.CommentServiceImpl;
import com.mysite.sbb.domain.user.SiteUser;
import com.mysite.sbb.domain.user.UserService;
import com.mysite.sbb.web.api.common.ApiResponse;
import com.mysite.sbb.web.api.common.v1.comment.dto.request.CommentRequestDTO;
import com.mysite.sbb.web.api.common.v1.comment.dto.request.CommentTargetType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Comment Controller", description = "댓글 컨트롤러")
@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentRestController {

    private final UserService userService;
    private final CommentServiceImpl commentServiceImpl;

    // 질문에 댓글 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{questionId}")
    public ResponseEntity<?> addComment(
            @PathVariable Long questionId,
            @RequestBody @Valid CommentRequestDTO dto,
            BindingResult bindingResult,
            Principal principal) {

        // 입력 검증
        if (bindingResult.hasErrors()) {
            log.error("Validation error: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(new ApiResponse(false, "댓글 형식에 맞지 않습니다."));
        }

        // 현재 로그인한 사용자를 가져옴
        SiteUser author = userService.getUser(principal.getName());

        // 댓글 추가
        Comment comment = commentServiceImpl.addComment(dto, author, questionId);

        // URL 생성 및 반환
        return createRedirectResponse(dto.targetType(), dto.targetId(), comment);
    }

    private ResponseEntity<?> createRedirectResponse(CommentTargetType targetType, Long targetId, Comment comment) {
        String redirectUrl;

        switch (targetType) {
            case CommentTargetType.QUESTION:
                redirectUrl = String.format("/question/detail/%d#comment%d", comment.getQuestionId(), comment.getId());
                break;

            case CommentTargetType.ANSWER:
                redirectUrl = String.format("/question/detail/%d#answer_%d", comment.getQuestionId(), targetId);
                break;

            default:
                log.error("Unsupported targetType: {}", targetType);
                return ResponseEntity.badRequest().body(new ApiResponse(false, "지원되지 않는 댓글 대상 타입입니다."));
        }

        return ResponseEntity
                .ok()
                .header("Location", redirectUrl)
                .body(new ApiResponse(true, "댓글 작성 성공"));
    }

}
