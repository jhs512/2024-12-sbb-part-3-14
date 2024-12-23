package com.mysite.sbb.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.controller.util.TestUtil;
import com.mysite.sbb.domain.dto.AnswerRequestDTO;
import com.mysite.sbb.domain.entity.Answer;
import com.mysite.sbb.domain.entity.Question;
import com.mysite.sbb.domain.entity.SiteUser;
import com.mysite.sbb.service.impl.AnswerServiceImpl;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import com.mysite.sbb.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnswerApiController.class)
class AnswerApiControllerTest {

    // 테스트 상수
    private static class TestConstants {
        static final Integer VALID_ANSWER_ID = 1;
        static final Integer VALID_QUESTION_ID = 1;
        static final Integer INVALID_ANSWER_ID = 999;
        static final String ADMIN_USER = "admin";
        static final String UNAUTHORIZED_USER = "hacker";
        static final String VOTED_USER = "votedUser";
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionServiceImpl questionService;

    @MockitoBean
    private AnswerServiceImpl answerService;

    @MockitoBean
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        reset(questionService, answerService, userService);
    }

    @Nested
    @DisplayName("답변 생성 API 테스트")
    class CreateAnswerTest {
        private AnswerRequestDTO validRequest;
        private Question question;
        private SiteUser user;
        private Answer answer;

        @BeforeEach
        void setUp() {
            // 테스트 데이터 준비
            validRequest = new AnswerRequestDTO();
            validRequest.setContent("테스트 답변 내용");

            question = new Question();
            question.setId(TestConstants.VALID_QUESTION_ID);

            user = new SiteUser();
            user.setUsername(TestConstants.ADMIN_USER);

            answer = new Answer();
            answer.setId(TestConstants.VALID_ANSWER_ID);
            answer.setQuestion(question);

            // 질문 ID 1을 요청하면 -> question 객체 반환
            when(questionService.getQuestion(1))
                    .thenReturn(question);

            // "admin" 사용자를 요청하면 -> user 객체 반환
            when(userService.getUser("admin"))
                    .thenReturn(user);

            // 답변 생성 요청하면 -> answer 객체 반환
            when(answerService.create(any(), anyString(), any()))
                    .thenReturn(answer);
        }

        @Test
        @DisplayName("인증되지 않은 사용자의 답변 생성 요청 - 401 응답")
        void createAnswer_Unauthorized() throws Exception {
            mockMvc.perform(post("/api/v1/answer/questions/{questionId}", TestConstants.VALID_QUESTION_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("유효한 답변 생성 요청 - 201 응답")
        void createAnswer_Success() throws Exception {
            mockMvc.perform(post("/api/v1/answer/questions/{questionId}", TestConstants.VALID_QUESTION_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/question/detail/1#answer_1")))
                    .andDo(print())
                    .andDo(TestUtil::printHTTP);

            verify(answerService).create(any(Question.class), anyString(), any(SiteUser.class));
        }
    }

    @Nested
    @DisplayName("답변 수정 API 테스트")
    class UpdateAnswerTest {
        private AnswerRequestDTO validRequest;
        private Answer answer;
        private SiteUser user;

        @BeforeEach
        void setUp() {
            validRequest = new AnswerRequestDTO();
            validRequest.setContent("수정된 답변 내용");

            user = new SiteUser();
            user.setUsername(TestConstants.ADMIN_USER);

            answer = new Answer();
            answer.setId(TestConstants.VALID_ANSWER_ID);
            answer.setAuthor(user);

            Question question = new Question();
            question.setId(TestConstants.VALID_QUESTION_ID);
            answer.setQuestion(question);

            // Mock 설정
            when(answerService.getAnswer(TestConstants.VALID_ANSWER_ID))
                    .thenReturn(answer);
            doNothing().when(answerService)
                    .modify(any(Answer.class), anyString());
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("유효한 답변 수정 요청 - 303 응답")
        void updateAnswer_Success() throws Exception {
            mockMvc.perform(put("/api/v1/answer/{id}", TestConstants.VALID_ANSWER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .with(csrf()))
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", containsString("/question/detail/1#answer_1")))
                    .andDo(print())
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @WithMockUser(username = "hacker")
        @DisplayName("다른 사용자의 답변 수정 요청 - 403 응답")
        void updateAnswer_Forbidden() throws Exception {
            mockMvc.perform(put("/api/v1/answer/{id}", TestConstants.VALID_ANSWER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .with(csrf()))
                    .andExpect(status().isForbidden())
                    .andDo(print())
                    .andDo(TestUtil::printHTTP);
        }
    }

    @Nested
    @DisplayName("답변 추천 API 테스트")
    class VoteAnswerTest {
        private Answer answer;
        private SiteUser user;

        @BeforeEach
        void setUp() {
            user = new SiteUser();
            user.setUsername(TestConstants.ADMIN_USER);

            answer = new Answer();
            answer.setId(TestConstants.VALID_ANSWER_ID);

            Question question = new Question();
            question.setId(TestConstants.VALID_QUESTION_ID);
            answer.setQuestion(question);

            // Mock 설정
            when(answerService.getAnswer(TestConstants.VALID_ANSWER_ID))
                    .thenReturn(answer);
            when(userService.getUser(TestConstants.ADMIN_USER))
                    .thenReturn(user);
            doNothing().when(answerService)
                    .vote(any(Answer.class), any(SiteUser.class));
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("유효한 답변 추천 요청 - 303 응답")
        void voteAnswer_Success() throws Exception {
            mockMvc.perform(post("/api/v1/answer/{id}/vote", TestConstants.VALID_ANSWER_ID)
                            .with(csrf()))
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", containsString("/question/detail/1#answer_1")))
                    .andDo(print())
                    .andDo(TestUtil::printHTTP);

            verify(answerService).vote(any(Answer.class), any(SiteUser.class));
        }
    }

    private AnswerRequestDTO createValidAnswerRequest() {
        AnswerRequestDTO dto = new AnswerRequestDTO();
        dto.setContent("테스트 답변 내용");
        return dto;
    }

    private AnswerRequestDTO createInvalidAnswerRequest() {
        AnswerRequestDTO dto = new AnswerRequestDTO();
        dto.setContent("");  // 빈 내용
        return dto;
    }
}