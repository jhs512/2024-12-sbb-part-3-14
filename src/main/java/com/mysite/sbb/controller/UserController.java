package com.mysite.sbb.controller;

import com.mysite.sbb.model.user.dto.UserRequestDTO;
import com.mysite.sbb.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 사용자 가입(Signup) 및 로그인(Login) 요청을 처리하는 컨트롤러 클래스.
 * <p>
 * 요청 흐름
 * <p>
 * [사용자 요청]
 * 1. 사용자가 /board/write 경로로 POST 요청
 *    ↓
 * [Controller]
 * 2. BoardController의 write 메서드 호출
 *    - @PreAuthorize("isAuthenticated()")로 인증 확인
 *    ↓
 * 3. @Valid로 폼 데이터 유효성 검증
 *    - 검증 실패: bindingResult.hasErrors() 처리
 *    ↓
 * [Service Layer]
 * 4. BoardService.save() 호출
 *    - 게시글 엔티티 생성 및 저장
 *    ↓
 * [Repository Layer]
 * 5. BoardRepository.save() 호출
 *    - 데이터베이스에 게시글 저장
 *    ↓
 * [사용자 응답]
 * 6. 저장 성공: 게시글 목록 페이지로 리다이렉트
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/user") // 모든 URL이 "/user"로 시작
public class UserController {

    private final UserServiceImpl userServiceImpl; // 사용자 관련 비즈니스 로직 처리

    /**
     * 사용자 가입 폼 페이지를 반환.
     *
     * @param userRequestDTO 폼 데이터 객체 (자동 바인딩)
     * @return 가입 폼 템플릿 경로
     */
    @GetMapping("/signup")
    public String signup(UserRequestDTO userRequestDTO) {
        return "signup_form"; // 가입 폼 페이지 반환
    }

    /**
     * 사용자 가입 요청 처리.
     *
     * @param userRequestDTO 폼 데이터 객체 (자동 바인딩)
     * @param bindingResult  폼 데이터 검증 결과 객체
     * @return 가입 성공 시 리다이렉트, 실패 시 가입 폼 반환
     */
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

    /**
     * 사용자 로그인 페이지를 반환.
     *
     * @return 로그인 폼 템플릿 경로
     */
    @GetMapping("/login")
    public String login() {
        return "login_form"; // 로그인 폼 페이지 반환
    }
}
