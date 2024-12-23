package com.mysite.sbb.controller.api;

import com.mysite.sbb.domain.user.dto.UserRequestDTO;
import com.mysite.sbb.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping("/signup")
    public String signup(@Valid UserRequestDTO userRequestDTO, BindingResult bindingResult) {
        // 1. 유효성 검증
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        // 2. 비밀번호 일치 수동 검증
        if (!userRequestDTO.getPassword1().equals(userRequestDTO.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        // 3. 사용자 생성
        try {
            userServiceImpl.create(
                    userRequestDTO.getUsername(),
                    userRequestDTO.getEmail(),
                    userRequestDTO.getPassword1());
        } catch (DataIntegrityViolationException e) {
            // 데이터 중복 (예: 동일한 이메일 또는 사용자 이름) 예외 처리
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            // 기타 예외 처리
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/"; // 가입 성공 시 메인 페이지로 리다이렉트
    }

}
