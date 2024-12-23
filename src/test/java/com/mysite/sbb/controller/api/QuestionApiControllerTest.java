package com.mysite.sbb.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.controller.util.TestUtil;
import com.mysite.sbb.domain.question.dto.QuestionRequestDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        @DisplayName("유효한 질문 생성 요청 - 성공")
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
        @WithMockUser
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