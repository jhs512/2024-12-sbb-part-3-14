package com.mysite.sbb.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.controller.util.TestUtil;
import com.mysite.sbb.domain.question.dto.QuestionRequestDTO;
import com.mysite.sbb.global.exception.AccessDeniedException;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionApiController.class)
class QuestionApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionServiceImpl questionService;

    @BeforeEach
    void setUp() {
        reset(questionService);
    }

    @Nested
    @DisplayName("질문 생성 API 테스트")
    class CreateQuestionTest {

        @Test
        @DisplayName("인증되지 않은 사용자의 질문 생성 요청 - 401 응답")
        void createQuestion_Unauthorized() throws Exception {
            // given
            QuestionRequestDTO request = createValidQuestionRequest();

            // when & then
            mockMvc.perform(post("/api/v1/question")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("유효한 질문 생성 요청 - 303 응답")
        void createQuestion_Success() throws Exception {
            // given
            QuestionRequestDTO request = createValidQuestionRequest();
            doNothing().when(questionService).create(any(QuestionRequestDTO.class), eq("admin"));

            // when & then
            mockMvc.perform(post("/api/v1/question")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .characterEncoding("UTF-8")  // 문자 인코딩 명시
                            .with(csrf()))
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", "/question/list"))
                    .andDo(TestUtil::printHTTP)
                    .andDo(result -> {
                        // 실패 시 더 자세한 정보를 보기 위해 응답 내용도 출력
                        System.out.println("\n=== 응답 내용 ===");
                        System.out.println(result.getResponse().getContentAsString());
                    });

            verify(questionService).create(any(QuestionRequestDTO.class), eq("admin"));
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("잘못된 형식의 질문 생성 요청 - 400 응답")
        void createQuestion_InvalidRequest() throws Exception {
            // given
            QuestionRequestDTO request = createInvalidQuestionRequest();

            // when & then
            mockMvc.perform(post("/api/v1/question")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("잘못된 JSON 형식의 요청 - 400 응답")
        void createQuestion_MalformedJson() throws Exception {

        }
    }

    @Nested
    @DisplayName("질문 수정 API 테스트")
    class UpdateQuestionTest {
        // 테스트에서 재사용할 ID 값들
        private final Integer VALID_QUESTION_ID = 1;    // 실제 존재하는 질문 ID
        private final Integer INVALID_QUESTION_ID = 999; // 존재하지 않는 질문 ID

        // 테스트에서 사용할 유효한 수정 요청 데이터
        private QuestionRequestDTO validUpdateRequest;

        @BeforeEach
        void setUp() {
            // 각 테스트 실행 전에 실행되는 초기화 메서드

            // 유효한 수정 요청 데이터 준비
            validUpdateRequest = new QuestionRequestDTO();
            validUpdateRequest.setSubject("수정된 제목");    // 제목 필수
            validUpdateRequest.setContent("수정된 내용");    // 내용 필수

            // 케이스 1: admin 사용자의 정상 수정
            doNothing().when(questionService)
                    .modify(eq(VALID_QUESTION_ID), any(QuestionRequestDTO.class), eq("admin"));

            // 케이스 2: 존재하지 않는 질문 수정
            doThrow(new DataNotFoundException("Question not found"))
                    .when(questionService)
                    .modify(eq(INVALID_QUESTION_ID), any(QuestionRequestDTO.class), any());

            // 케이스 3: 권한 없는 사용자 수정
            doThrow(new AccessDeniedException("Not authorized"))
                    .when(questionService)
                    .modify(eq(VALID_QUESTION_ID), any(QuestionRequestDTO.class), eq("hacker"));
        }

        @Test
        @DisplayName("인증되지 않은 사용자의 질문 수정 요청 - 401 응답")
        void updateQuestion_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/v1/question/{id}", VALID_QUESTION_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("존재하지 않는 질문 수정 요청 - 404 응답")
        void updateQuestion_NotFound() throws Exception {
            mockMvc.perform(put("/api/v1/question/{id}", INVALID_QUESTION_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validUpdateRequest))
                    .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("유효하지 않은 질문 수정 요청 - 400 응답")
        void updateQuestion_InvalidRequest() throws Exception {
            // given
            QuestionRequestDTO invalidRequest = new QuestionRequestDTO();
            invalidRequest.setSubject("");  // 빈 제목
            invalidRequest.setContent("");  // 빈 내용

            // when & then
            mockMvc.perform(put("/api/v1/question/{id}", VALID_QUESTION_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("유효한 질문 수정 요청 - 303 응답")
        void updateQuestion_Success() throws Exception {
            mockMvc.perform(put("/api/v1/question/{id}", VALID_QUESTION_ID).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", "/question/detail/1"))
                    .andDo(TestUtil::printHTTP);

            verify(questionService).modify(eq(VALID_QUESTION_ID), any(QuestionRequestDTO.class), eq("admin"));
        }

        @Test
        @WithMockUser(username = "hacker")
        @DisplayName("다른 사용자의 질문 수정 요청 - 403 응답")
        void updateQuestion_Forbidden() throws Exception {
            mockMvc.perform(put("/api/v1/question/{id}", VALID_QUESTION_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isForbidden())
                    .andDo(TestUtil::printHTTP);
        }
    }

    @Nested
    @DisplayName("질문 삭제 API 테스트")
    class DeleteQuestionTest {
        // 테스트에서 재사용할 ID 값들
        private final Integer VALID_QUESTION_ID = 1;    // 실제 존재하는 질문 ID
        private final Integer INVALID_QUESTION_ID = 999; // 존재하지 않는 질문 ID

        @BeforeEach
        void setUp() {
            // 케이스 1: admin 사용자의 정상 삭제
            doNothing().when(questionService)
                    .delete(eq(VALID_QUESTION_ID), eq("admin"));

            // 케이스 2: 존재하지 않는 질문 삭제
            doThrow(new DataNotFoundException("Question not found"))
                    .when(questionService)
                    .delete(eq(INVALID_QUESTION_ID), any());

            // 케이스 3: 권한 없는 사용자 삭제
            doThrow(new AccessDeniedException("Not authorized"))
                    .when(questionService)
                    .delete(eq(VALID_QUESTION_ID), eq("hacker"));
        }


        @Test
        @DisplayName("인증되지 않은 사용자의 질문 삭제 요청 - 401 응답")
        void deleteQuestion_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/v1/question/{id}", VALID_QUESTION_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andDo(TestUtil::printHTTP);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("존재하지 않는 질문 삭제 요청 - 404 응답")
        void deleteQuestion_NotFound() throws Exception {
            mockMvc.perform(delete("/api/v1/question/{id}", INVALID_QUESTION_ID)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andDo(TestUtil::printHTTP);

            // 서비스 메서드 호출 확인
            verify(questionService).delete(eq(INVALID_QUESTION_ID), eq("admin"));
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("유효한 질문 삭제 요청 - 303 응답")
        void deleteQuestion_Success() throws Exception {
            // 정상적인 삭제 요청
            mockMvc.perform(delete("/api/v1/question/{id}", VALID_QUESTION_ID)
                            .with(csrf()))
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", "/"))
                    .andDo(TestUtil::printHTTP);

            // 서비스 메서드 호출 확인
            verify(questionService).delete(eq(VALID_QUESTION_ID), eq("admin"));
        }

        @Test
        @WithMockUser(username = "hacker")
        @DisplayName("다른 사용자의 질문 삭제 요청 - 403 응답")
        void deleteQuestion_Forbidden() throws Exception {
            // 권한 없는 사용자로 삭제 요청
            mockMvc.perform(delete("/api/v1/question/{id}", VALID_QUESTION_ID)
                            .with(csrf()))
                    .andExpect(status().isForbidden())
                    .andDo(TestUtil::printHTTP);

            // 서비스 메서드 호출 확인
            verify(questionService).delete(eq(VALID_QUESTION_ID), eq("hacker"));
        }
    }

    @Nested
    @DisplayName("질문 추천 API 테스트")
    class VoteQuestionTest {

        @Test
        @DisplayName("인증되지 않은 사용자의 질문 추천 요청 - 401 응답")
        void voteQuestion_Unauthorized() throws Exception {

        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("존재하지 않는 질문 추천 요청 - 404 응답")
        void voteQuestion_NotFound() throws Exception {

        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("유효한 질문 추천 요청 - 303 응답")
        void voteQuestion_Success() throws Exception {

        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("이미 추천한 질문 재추천 요청 - 400 응답")
        void voteQuestion_AlreadyVoted() throws Exception {

        }

    }



    private QuestionRequestDTO createValidQuestionRequest() {
        QuestionRequestDTO dto = new QuestionRequestDTO();
        dto.setSubject("테스트 제목");
        dto.setContent("테스트 내용");
        return dto;
    }

    private QuestionRequestDTO createInvalidQuestionRequest() {
        QuestionRequestDTO dto = new QuestionRequestDTO();
        dto.setSubject("");  // 빈 제목
        dto.setContent("");  // 빈 내용
        return dto;
    }
}