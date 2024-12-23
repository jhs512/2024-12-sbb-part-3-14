package com.mysite.sbb.controller.view;


import com.mysite.sbb.domain.question.dto.QuestionListResponseDTO;
import com.mysite.sbb.domain.question.dto.QuestionRequestDTO;
import com.mysite.sbb.service.impl.AnswerServiceImpl;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import com.mysite.sbb.service.impl.UserServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ViewController의 테스트 클래스
 * <p>
 * &#064;WebMvcTest:  웹 계층 관련 빈만 로드하여 테스트 (전체 애플리케이션 컨텍스트를 로드하지 않음)
 * &#064;ExtendWith:  Mockito 확장 기능을 사용하여 목 객체 생성 및 주입
 */
@WebMvcTest(ViewController.class)  // ViewController만 테스트하도록 설정
@ExtendWith(MockitoExtension.class)  // Mockito 확장 기능 사용
class ViewControllerTest {

    // MockMvc: 실제 서버 없이 스프링 MVC 동작을 재현하는 테스트 프레임워크
    @Autowired
    private MockMvc mockMvc;

    // @MockBean: 스프링 컨테이너에 목 객체를 빈으로 등록
    @MockitoBean
    private QuestionServiceImpl questionService;     // 질문 서비스 모킹

    @MockitoBean
    private AnswerServiceImpl answerService;         // 답변 서비스 모킹

    @MockitoBean
    private UserServiceImpl userService;             // 사용자 서비스 모킹

    /**
     * Spring Security 테스트 설정
     * - 인증이 필요한 URL과 허용된 URL을 구분하여 설정
     * - 로그인, 로그아웃 처리 설정
     * - CSRF 보호 설정
     */
    @TestConfiguration
    static class SecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            // 인증이 필요한 URL 설정
                            .requestMatchers("/question/create",
                                             "/question/modify/**",
                                             "/question/delete/**",
                                             "/answer/**").authenticated()
                            // 나머지 요청은 모두 허용
                            .anyRequest().permitAll()
                    )
                    .formLogin(form -> form
                            .loginPage("/user/login")
                            .defaultSuccessUrl("/")
                            .permitAll()
                    )
                    .logout((logout) -> logout
                            .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                            .logoutSuccessUrl("/")
                            .invalidateHttpSession(true)
                    )
                    .csrf(csrf -> csrf
                            .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                    );


            return http.build();
        }
    }

    /**
     * 루트 경로 접속 시 질문 목록 페이지로 리다이렉트되는지 테스트
     * <p>
     * 검증 내용:
     * * 1. HTTP 상태 코드가 리다이렉션(3xx)인지 확인
     * * 2. 리다이렉트 URL이 "/question/list"인지 확인
     */
    @Test
    @DisplayName("1 - 루트 경로 접속 테스트")
    void rootPage_RedirectToQuestionList() throws Exception {
        // when: 루트 경로로 GET 요청
        // then: 리다이렉션 검증
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())     // 리다이렉션 상태 코드 확인
                .andExpect(redirectedUrl("/question/list")) // 리다이렉트 URL 확인
                .andDo(print())    // 테스트 결과 출력
                .andDo(this::printHTTP);
    }

    @Test
    @DisplayName("2 - 질문 목록 페이지 접속 테스트")
    void questionList_ReturnListPage() throws Exception {
        // given ( 준비 )
        List<QuestionListResponseDTO> questionDTOs = new ArrayList<>();
        Page<QuestionListResponseDTO> questionDTOPage = new PageImpl<>(
                questionDTOs,
                PageRequest.of(0, 10),
                0
        );

        // questionService.getList() 메서드의 동작을 모킹
        given(questionService.getList(anyInt(), anyString())).willReturn(questionDTOPage);

        // when & then (실행 및 검증)
        mockMvc.perform(get("/question/list")  // GET 요청 실행
                        .param("page", "0")           // 페이지 파라미터 추가
                        .param("kw", ""))             // 검색어 파라미터 추가
                .andExpect(status().isOk())       // HTTP 200 상태 코드 검증
                .andExpect(view().name("question_list"))  // 뷰 이름 검증
                .andExpect(model().attributeExists("paging"))  // 모델 속성 존재 검증
                .andDo(print())  // 테스트 결과 출력
                .andDo(result -> {
                    printHTTP(result);
                    printModelAndView(result);
                });
    }

    @Test
    @DisplayName("3 - 회원가입 페이지 접속 테스트")
    void testSignupPage() throws Exception {
        mockMvc.perform(get("/users/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup_form"))
                .andDo(print())
                .andDo(this::printModelAndView);
    }

    @Test
    @DisplayName("4 - 로그인 페이지 접속 테스트")
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login_form"))
                .andDo(print())
                .andDo(this::printModelAndView);
    }

    @Test
    @DisplayName("5 - 인증되지 않는 사용자의 질문 생성 페이지 접속 테스트")
    void testQuestionCreatePageWithoutAuth() throws Exception {
        mockMvc.perform(get("/question/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"))
                .andDo(print())
                .andDo(result -> {
                    printHTTP(result);
                    printModelAndView(result);
                });
    }

    @Test
    @WithMockUser
    @DisplayName("6 - 인증된 사용자의 질문 생성 페이지 접속 테스트")
    void testQuestionCreatePageWithAuth() throws Exception {
        // QuestionRequestDTO를 모델에 추가
        QuestionRequestDTO questionRequestDTO = new QuestionRequestDTO();

        mockMvc.perform(get("/question/create")
                        .with(csrf())
                .flashAttr("questionRequestDTO", questionRequestDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"))
                .andExpect(model().attributeExists("questionRequestDTO"))
                .andDo(print())
                .andDo(result -> {
                    printHTTP(result);
                    printModelAndView(result);
                });
    }

    @Test
    @DisplayName("7 - 질문 목록 페이징 테스트")
    void testQuestionList_WithPaging() throws Exception {

    }

    @Test
    @DisplayName("8 - 질문 목록 페이징 테스트")
    void testQuestionList_WithPaging2() throws Exception {

    }

    @Test
    @DisplayName("9 - 질문 상세 페이지 테스트")
    void testQuestionDetail() throws Exception {

    }

    @SneakyThrows
    private void printHTTP(MvcResult result) {
        System.out.println("\n=== HTTP 응답 정보 ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Headers: " + result.getResponse().getHeaderNames());
        System.out.println("Content: " + result.getResponse().getContentAsString());
        System.out.println("URL: " + result.getResponse().getRedirectedUrl());
    }

    private void printModelAndView(MvcResult result) {
        System.out.println("\n=== 모델 정보 ===");
        ModelAndView mav = result.getModelAndView();
        if (mav != null) {
            System.out.println("View: " + mav.getViewName());
            System.out.println("Model: " + mav.getModel());
        }
    }


}