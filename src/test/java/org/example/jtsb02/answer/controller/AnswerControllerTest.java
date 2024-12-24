package org.example.jtsb02.answer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.service.AnswerService;
import org.example.jtsb02.common.security.SecurityConfig;
import org.example.jtsb02.common.util.CommonUtil;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.service.MemberService;
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
import org.springframework.security.test.context.support.WithMockUser;
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

    @MockitoBean
    private MemberService memberService;

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
    @WithMockUser(username = "onlyTest")
    void createAnswer() throws Exception {
        //given
        String url = "/answer/create/1";
        MemberDto member = createMember();
        when(memberService.getMember("onlyTest")).thenReturn(member);

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("id", "1")
            .param("content", "내용"));

        //then
        result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/question/detail/1"));
        verify(answerService, times(1)).createAnswer(any(Long.class),
            any(AnswerForm.class), any(MemberDto.class));
    }

    @Test
    @DisplayName("POST /answer/create/{questionId} - Answer 생성 실패 - 내용이 비었을 경우")
    @WithMockUser(username = "onlyTest")
    void createAnswerEmptyContent() throws Exception {
        //given
        String url = "/answer/create/1";
        MemberDto member = createMember();
        when(memberService.getMember("onlyTest")).thenReturn(member);
        Question question = createQuestion();
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

    @Test
    @DisplayName("POST /answer/create/{questionId} - Answer 생성 실패 - 내용이 2000자 이상일 경우")
    @WithMockUser(username = "onlyTest")
    void createAnswerLongContent() throws Exception {
        //given
        String url = "/answer/create/1";
        MemberDto member = createMember();
        when(memberService.getMember("onlyTest")).thenReturn(member);
        Question question = createQuestion();
        when(questionService.getQuestion(1L)).thenReturn(QuestionDto.fromQuestion(question));

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("id", "1")
            .param("content", generateStringOfLength(2001)));

        //then
        result.andExpect(status().isOk())
            .andExpect(view().name("question/detail"))
            .andExpect(model().attributeExists("answerForm"));
    }

    @Test
    @DisplayName("GET /answer/modify/{answerId} - Answer 수정폼으로 이동")
    @WithMockUser(username = "onlyTest")
    void modifyAnswerForm() throws Exception {
        //given
        String url = "/answer/modify/1";
        Question question = createQuestion();
        Answer answer = createAnswer(question);
        when(answerService.getAnswer(1L)).thenReturn(AnswerDto.fromAnswer(answer));

        //when
        ResultActions result = mockMvc.perform(get(url));

        //then
        result.andExpect(status().isOk())
            .andExpect(view().name("answer/form/modify"))
            .andExpect(model().attributeExists("answerForm"));
    }

    @Test
    @DisplayName("POST /answer/modify/{answerId} - Answer 수정")
    @WithMockUser(username = "onlyTest")
    void modifyAnswer() throws Exception {
        //given
        String url = "/answer/modify/1";
        Question question = createQuestion();
        Answer answer = createAnswer(question);
        when(answerService.getAnswer(1L)).thenReturn(AnswerDto.fromAnswer(answer));

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("id", "1")
            .param("content", "수정된 내용"));

        //then
        result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/question/detail/1"));
        verify(answerService, times(1)).modifyAnswer(any(Long.class), any(AnswerForm.class));
    }

    @Test
    @DisplayName("GET /answer/delete/{answerId} - Answer 삭제")
    @WithMockUser(username = "onlyTest")
    void deleteAnswer() throws Exception {
        //given
        String url = "/answer/delete/1";
        Question question = createQuestion();
        Answer answer = createAnswer(question);
        when(answerService.getAnswer(1L)).thenReturn(AnswerDto.fromAnswer(answer));

        //when
        ResultActions result = mockMvc.perform(get(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("id", "1"));

        //then
        result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/question/detail/1"));
        verify(answerService, times(1)).deleteAnswer(any(AnswerDto.class));
    }

    private MemberDto createMember() {
        return MemberDto.builder()
            .id(1L)
            .memberId("onlyTest")
            .nickname("onlyTest")
            .email("onlyTest@test.com")
            .build();
    }

    private Question createQuestion() {
        return Question.builder()
            .id(1L)
            .subject("test subject")
            .content("test content")
            .createdAt(LocalDateTime.now())
            .hits(1)
            .answers(new ArrayList<>())
            .author(Member.builder()
                .id(1L)
                .memberId("onlyTest")
                .nickname("onlyTest")
                .password("onlyTest")
                .email("onlyTest@gmail.com")
                .build())
            .build();
    }

    private Answer createAnswer(Question question) {
        return Answer.builder()
            .id(1L)
            .content("test content")
            .createdAt(LocalDateTime.now())
            .question(question)
            .author(Member.builder()
                .id(1L)
                .memberId("onlyTest")
                .nickname("onlyTest")
                .password("onlyTest")
                .email("onlyTest@gmail.com")
                .build())
            .build();
    }

    private String generateStringOfLength(int length) {
        return "a".repeat(length);
    }
}