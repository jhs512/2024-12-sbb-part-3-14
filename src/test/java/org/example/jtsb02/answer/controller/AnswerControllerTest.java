package org.example.jtsb02.answer.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.service.AnswerService;
import org.example.jtsb02.common.security.SecurityConfig;
import org.example.jtsb02.common.util.CommonUtil;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.service.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AnswerController.class)
@Import({SecurityConfig.class, CommonUtil.class})
class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnswerService answerService;

    @MockitoBean
    private QuestionService questionService;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("빈 이름 출력")
    void printBeanNames() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Arrays.stream(beanNames)
            .sorted() // 정렬해서 보기 쉽게 출력
            .forEach(System.out::println);
    }

    @Test
    @DisplayName("POST /answer/create/{questionId} - Answer 생성 성공")
    void createAnswer() throws Exception {
        //given
        String url = "/answer/create/1";

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("id", "1")
            .param("content", "내용"));

        //then
        result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/question/detail/1"));
        verify(answerService, times(1)).createAnswer(any(Long.class), any(AnswerForm.class));
    }

    @Test
    @DisplayName("POST /answer/create/{questionId} - Answer 생성 실패 - 유효성 검증")
    void createAnswerEmptyContent() throws Exception {
        //given
        String url = "/answer/create/1";
        Question question = createQuestion(1L);
        when(questionService.getQuestion(1L)).thenReturn(QuestionDto.fromQuestion(question));

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("id", "1")
            .param("content", ""));

        //then
        result.andExpect(status().isOk())
            .andExpect(view().name("question/detail"))
            .andExpect(model().attributeExists("answerForm"));
    }

    private Question createQuestion(Long id) {
        return Question.builder()
            .id(id)
            .subject("test subject")
            .content("test content")
            .createdAt(LocalDateTime.now())
            .hits(1)
            .answers(new ArrayList<>())
            .build();
    }
}