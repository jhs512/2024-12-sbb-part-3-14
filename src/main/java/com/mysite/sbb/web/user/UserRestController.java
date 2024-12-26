package com.mysite.sbb.web.user;

import com.mysite.sbb.web.common.dto.response.ApiResponse;
import com.mysite.sbb.web.user.dto.request.UserRequestDTO;
import com.mysite.sbb.domain.user.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                    .body(new ApiResponse(false, "signupFailed"));
        }

    }

}
