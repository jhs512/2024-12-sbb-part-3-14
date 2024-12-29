package com.mysite.sbb.web;

import com.mysite.sbb.controller.util.QuestionTestFixture;
import com.mysite.sbb.controller.util.TestUtil;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionListResponseDTO;
import com.mysite.sbb.web.api.v1.question.dto.request.QuestionRequestDTO;
import com.mysite.sbb.global.util.CommonUtil;
import com.mysite.sbb.domain.answer.service.AnswerServiceImpl;
import com.mysite.sbb.domain.question.service.QuestionServiceImpl;
import com.mysite.sbb.domain.user.service.UserServiceImpl;
import com.mysite.sbb.web.view.ViewController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ViewController의 테스트 클래스
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ViewController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 목 객체 초기화
        reset(questionService, answerService, userService);
    }

    /**
     * Spring Security 테스트 설정
     * - 인증이 필요한 URL과 허용된 URL을 구분하여 설정
     * - 로그인, 로그아웃 처리 설정
     * - CSRF 보호 설정
     */
    @TestConfiguration
    @AutoConfigureBefore(SecurityAutoConfiguration.class)
    static class TestConfig {
        @Bean
        public CommonUtil commonUtil() {
            return new CommonUtil();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            // 인증이 필요한 URL 설정
                            .requestMatchers(
                                    "/question/create/**",
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
                    .csrf(csrf -> csrf.disable());


            return http.build();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring()
                    .requestMatchers("/h2-console/**");
        }
    }


    @Nested
    @DisplayName("기본 페이지 접근 테스트")
    class BasicPageAccessTest {
        @Test
        @DisplayName("루트 페이지 접속시 질문 목록으로 리다이렉트")
        void rootPage_RedirectToQuestionList() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/question/list"))
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @DisplayName("회원가입 페이지 접속")
        void signupPage_ShouldReturnSignupForm() throws Exception {
            mockMvc.perform(get("/users/signup"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("signup_form"))
                    .andDo(TestUtil::printModelAndView);
        }

        @Test
        @DisplayName("로그인 페이지 접속")
        void loginPage_ShouldReturnLoginForm() throws Exception {
            mockMvc.perform(get("/users/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login_form"))
                    .andDo(TestUtil::printModelAndView);
        }
    }

    @Nested
    @DisplayName("질문 목록 페이징 테스트")
    class QuestionListPagingTest {

        @Test
        @DisplayName("기본 페이징 - 첫 페이지 조회")
        void questionList_DefaultPaging() throws Exception {
            // given
            Page<QuestionListResponseDTO> questionPage = QuestionTestFixture.builder()
                    .withTotalCount(15)
                    .withPageSize(10)
                    .withCurrentPage(0)
                    .build();

            given(questionService.getList(anyInt(), anyString()))
                    .willReturn(questionPage);

            // when & then
            mockMvc.perform(get("/question/list")
                            .param("page", "0")
                            .param("kw", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("question_list"))
                    .andExpect(model().attributeExists("paging"))
                    .andExpect(model().attribute("paging",
                            allOf(
                                    hasProperty("totalElements", equalTo(15L)),
                                    hasProperty("totalPages", equalTo(2)),
                                    hasProperty("size", equalTo(10))
                            )))
                    .andDo(TestUtil::printTestResults);
        }

        @Test
        @DisplayName("검색 조건이 있는 페이징")
        void questionList_WithSearchKeyword() throws Exception {
            // given
            Page<QuestionListResponseDTO> questionPage = QuestionTestFixture.builder()
                    .withTotalCount(30)
                    .withPageSize(10)
                    .withCurrentPage(0)
                    .withSearchKeyword("작성자2")
                    .build();

            given(questionService.getList(0, "작성자2"))
                    .willReturn(questionPage);

            // when & then
            mockMvc.perform(get("/question/list")
                            .param("page", "0")
                            .param("kw", "작성자2"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("paging"))
                    .andExpect(model().attribute("paging",
                            allOf(
                                    hasProperty("totalElements", equalTo(10L)),
                                    hasProperty("totalPages", equalTo(1)),
                                    hasProperty("size", equalTo(10))
                            )))
                    .andDo(TestUtil::printTestResults);

            verify(questionService).getList(0, "작성자2");
        }
    }

    @Nested
    @DisplayName("질문 생성 페이지 접근 테스트")
    class QuestionCreatePageTest {

        @Test
        @DisplayName("미인증 사용자 접근시 로그인 페이지로 리다이렉트")
        void questionCreatePage_WithoutAuth_ShouldRedirectToLogin() throws Exception {
            mockMvc.perform(get("/question/create"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"))
                    .andDo(TestUtil::printTestResults);
        }

        @Test
        @WithMockUser
        @DisplayName("인증된 사용자는 질문 생성 페이지 접근 가능")
        void questionCreatePage_WithAuth_ShouldReturnCreateForm() throws Exception {
            QuestionRequestDTO questionRequestDTO = new QuestionRequestDTO();

            mockMvc.perform(get("/question/create")
                            .with(csrf())
                            .flashAttr("questionRequestDTO", questionRequestDTO))
                    .andExpect(status().isOk())
                    .andExpect(view().name("question_form"))
                    .andExpect(model().attributeExists("questionRequestDTO"))
                    .andDo(TestUtil::printTestResults);
        }
    }

    @Nested
    @DisplayName("질문 상세 페이지 접근 테스트 (TODO)")
    class QuestionDetailPageTest {
        @Test
        @DisplayName("질문 상세 페이지 테스트 (마크다운 포함)")
        void testQuestionDetail() throws Exception {
            // TODO
        }

        @Test
        @DisplayName("답변 정렬 기능 테스트")
        void testAnswerSorting() throws Exception {
            // TODO
        }

        @Test
        @WithMockUser
        @DisplayName("로그인 사용자의 답변 작성 폼 접근 테스트")
        void testAnswerFormForAuthenticatedUser() throws Exception {
            // TODO
        }
    }


    /**
     * 테스트용 질문 목록 생성
     */
    private List<QuestionListResponseDTO> createTestQuestions(int count) {
        List<QuestionListResponseDTO> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            QuestionListResponseDTO dto = new QuestionListResponseDTO();
            dto.setId(i + 1);
            dto.setSubject("테스트 질문 " + (i + 1));
            dto.setContent("테스트 내용 " + (i + 1));
            dto.setCreateDate(LocalDateTime.now().minusDays(i));
            dto.setAuthorName("테스트 작성자" + i % 3);
            dto.setAnswerCount(i % 3); // 0~2개의 답변
            questions.add(dto);
        }
        return questions;
    }

}