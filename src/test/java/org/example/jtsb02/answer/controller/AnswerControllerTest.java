package org.example.jtsb02.answer.controller;

import static org.example.util.TestHelper.createAnswer;
import static org.example.util.TestHelper.createAnswerForm;
import static org.example.util.TestHelper.createMember;
import static org.example.util.TestHelper.createQuestion;
import static org.example.util.TestHelper.createQuestionForm;
import static org.example.util.TestHelper.generateStringOfLength;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.service.AnswerService;
import org.example.jtsb02.common.security.SecurityConfig;
import org.example.jtsb02.common.util.CommonUtil;
import org.example.jtsb02.member.dto.MemberDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    void createAnswerTest() throws Exception {
        //given
        String url = "/answer/create/1";
        MemberDto member = createMember();
        Question question = createQuestion(1L, createQuestionForm("test subject", "test content"));
        List<AnswerDto> answers = new ArrayList<>();
        Page<AnswerDto> answerPage = new PageImpl<>(answers);
        when(questionService.getQuestion(1L)).thenReturn(QuestionDto.fromQuestion(question, answerPage));
        when(memberService.getMember("onlyTest")).thenReturn(member);
        when(answerService.createAnswer(eq(1L), any(AnswerForm.class), eq(member))).thenReturn(1L);

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("id", "1")
            .param("content", "내용"));

        //then
        result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/question/detail/1?page=1#answer_1"));
        verify(answerService, times(1)).createAnswer(any(Long.class),
            any(AnswerForm.class), any(MemberDto.class));
    }

    @Test
    @DisplayName("POST /answer/create/{questionId} - Answer 생성 실패 - 내용이 비었을 경우")
    @WithMockUser(username = "onlyTest")
    void createAnswerEmptyContentTest() throws Exception {
        //given
        String url = "/answer/create/1";
        MemberDto member = createMember();
        when(memberService.getMember("onlyTest")).thenReturn(member);
        Question question = createQuestion(1L, createQuestionForm("test subject", "test content"));
        List<AnswerDto> answers = new ArrayList<>();
        Page<AnswerDto> answerPage = new PageImpl<>(answers);
        when(questionService.getQuestion(1L)).thenReturn(QuestionDto.fromQuestion(question, answerPage));

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
    void createAnswerLongContentTest() throws Exception {
        //given
        String url = "/answer/create/1";
        MemberDto member = createMember();
        when(memberService.getMember("onlyTest")).thenReturn(member);
        Question question = createQuestion(1L, createQuestionForm("test subject", "test content"));
        List<AnswerDto> answers = new ArrayList<>();
        Page<AnswerDto> answerPage = new PageImpl<>(answers);
        when(questionService.getQuestion(1L)).thenReturn(QuestionDto.fromQuestion(question, answerPage));

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
    void modifyAnswerFormTest() throws Exception {
        //given
        String url = "/answer/modify/1";
        Question question = createQuestion(1L, createQuestionForm("test subject", "test content"));
        Answer answer = createAnswer(createAnswerForm("test content"), question);
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
    void modifyAnswerTest() throws Exception {
        //given
        String url = "/answer/modify/1";
        Question question = createQuestion(1L, createQuestionForm("test subject", "test content"));
        Answer answer = createAnswer(createAnswerForm("test content"), question);
        when(answerService.getAnswer(1L)).thenReturn(AnswerDto.fromAnswer(answer));

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("id", "1")
            .param("content", "수정된 내용"));

        //then
        result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/question/detail/1#answer_1"));
        verify(answerService, times(1)).modifyAnswer(any(Long.class), any(AnswerForm.class));
    }

    @Test
    @DisplayName("GET /answer/delete/{answerId} - Answer 삭제")
    @WithMockUser(username = "onlyTest")
    void deleteAnswerTest() throws Exception {
        //given
        String url = "/answer/delete/1";
        Question question = createQuestion(1L, createQuestionForm("test subject", "test content"));
        Answer answer = createAnswer(createAnswerForm("test content"), question);
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
}