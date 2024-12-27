package com.mysite.sbb.web.comment;

import com.mysite.sbb.domain.comment.Comment;
import com.mysite.sbb.domain.comment.CommentService;
import com.mysite.sbb.domain.user.SiteUser;
import com.mysite.sbb.domain.user.UserService;
import com.mysite.sbb.web.api.ApiResponse;
import com.mysite.sbb.web.comment.dto.request.CommentRequestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Comment Controller", description = "댓글 컨트롤러")
@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentRestController {

    private final UserService userService;
    private final CommentService commentService;

    // 질문에 댓글 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/{questionId}")
    public ResponseEntity<?> addCommentToQuestion(@PathVariable Long questionId,
                                                  @RequestBody @Valid CommentRequestDTO commentRequestDTO,
                                                  BindingResult bindingResult,
                                                  Principal principal) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "댓글 형식에 맞지 않습니다."));
        }

        // 현재 로그인한 사용자를 가져옴
        SiteUser author = userService.getUser(principal.getName());

        // 댓글 추가
        Comment comment = commentService.addCommentToQuestion(commentRequestDTO, author);

        // OK(URL 반환)
        String redirectUrl = String.format("/question/detail/%d#comment%d", questionId, comment.getId());
        return ResponseEntity.ok(redirectUrl);
    }

}
