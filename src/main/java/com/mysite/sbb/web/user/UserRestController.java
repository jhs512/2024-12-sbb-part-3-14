package com.mysite.sbb.web.user;

import com.mysite.sbb.domain.user.UserServiceImpl;
import com.mysite.sbb.web.api.ApiResponse;
import com.mysite.sbb.web.user.dto.request.ChangeRequestDTO;
import com.mysite.sbb.web.user.dto.request.ForgotRequestDTO;
import com.mysite.sbb.web.user.dto.request.UserRequestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Auth Controller", description = "유저 권한 컨트롤러")
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@RestController
public class UserRestController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody UserRequestDTO userRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "입력값이 올바르지 않습니다."));
        }

        if (!userRequestDTO.getPassword1().equals(userRequestDTO.getPassword2())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "비밀번호가 일치하지 않습니다"));
        }

        try {
            userServiceImpl.create(userRequestDTO.getUsername(), userRequestDTO.getEmail(), userRequestDTO.getPassword1());
            return ResponseEntity.ok(new ApiResponse(true, "회원가입이 완료되었습니다."));
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "이미 등록된 ID 혹은 이메일입니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "회원가입 실패"));
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @Valid @RequestBody ForgotRequestDTO forgotRequestDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "ID 또는 이메일을 확인해주세요."));
        }

        boolean emailSent = userServiceImpl.sendPasswordResetEmail(forgotRequestDTO);

        if (emailSent) {
            return ResponseEntity.ok(new ApiResponse(true, "새 비밀번호가 이메일에 전송되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "ID 또는 이메일을 확인해주세요."));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody ChangeRequestDTO changeRequestDTO,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "비밀번호를 확인해주세요."));
        }

        boolean isChanged = userServiceImpl.changePassword(changeRequestDTO, principal.getName());

        if (isChanged) {
            return ResponseEntity.ok(new ApiResponse(true, "새 비밀번호가 이메일에 전송되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "비밀번호를 확인해주세요."));
        }
    }

}
