package com.mysite.sbb.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.web.user.UserRestController;
import com.mysite.sbb.controller.util.TestUtil;
import com.mysite.sbb.web.user.dto.request.UserRequestDTO;
import com.mysite.sbb.domain.user.SiteUser;
import com.mysite.sbb.domain.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceImpl userService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            // 인증이 필요한 URL 설정
                            .requestMatchers("/api/v1/user/signup").permitAll() // 회원가입 경로는 허용
                            .anyRequest().authenticated()
                    )
                    .csrf(AbstractHttpConfigurer::disable);


            return http.build();
        }

    }

    @Nested
    @DisplayName("회원가입 API 테스트")
    class SignupTest {

        @BeforeEach
        void setUp() {


            // 기본 성공 케이스
            // 기본 성공 케이스 - void가 아닌 메서드는 when().thenReturn() 사용
            when(userService.create(anyString(), anyString(), anyString()))
                    .thenReturn(new SiteUser()); // 또는 필요한 반환값

            // 중복 사용자 케이스
            doThrow(new DataIntegrityViolationException("Duplicate user"))
                    .when(userService)
                    .create(eq("existingUser"), anyString(), anyString());
        }

        @Test
        @DisplayName("유효한 회원가입 요청 - 성공")
        void signup_Success() throws Exception {
            // given
            UserRequestDTO request = createValidSignupRequest();

            // when & then
            mockMvc.perform(post("/api/v1/user/signup")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("redirect:/"))
                    .andDo(TestUtil::printHTTP);

            verify(userService).create(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword1()
            );
        }

        @Test
        @DisplayName("비밀번호 불일치 - 실패")
        void signup_PasswordMismatch() throws Exception {
            // given
            UserRequestDTO request = new UserRequestDTO();
            request.setUsername("testuser");
            request.setEmail("test@test.com");
            request.setPassword1("password123");
            request.setPassword2("password456"); // 불일치하는 비밀번호

            // when & then
            mockMvc.perform(post("/api/v1/user/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("signup_form"))
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @DisplayName("중복된 사용자명 회원가입 요청 - 실패")
        void signup_DuplicateUser() throws Exception {
            // given
            UserRequestDTO request = new UserRequestDTO();
            request.setUsername("existingUser"); // 이미 존재하는 사용자명
            request.setEmail("test@test.com");
            request.setPassword1("password123");
            request.setPassword2("password123");

            // when & then
            mockMvc.perform(post("/api/v1/user/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("signup_form"))
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @DisplayName("유효하지 않은 이메일 형식 - 실패")
        void signup_InvalidEmail() throws Exception {
            // given
            UserRequestDTO request = new UserRequestDTO();
            request.setUsername("testuser");
            request.setEmail("invalid-email"); // 잘못된 이메일 형식
            request.setPassword1("password123");
            request.setPassword2("password123");

            // when & then
            mockMvc.perform(post("/api/v1/user/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("signup_form"))
                    .andDo(TestUtil::printHTTP);
        }

        private UserRequestDTO createValidSignupRequest() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.setUsername("testuser");
            dto.setEmail("test@test.com");
            dto.setPassword1("password123");
            dto.setPassword2("password123");
            return dto;
        }
    }
}