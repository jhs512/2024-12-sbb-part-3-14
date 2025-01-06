package org.example.jtsb02.question.controller;

import static org.example.util.TestHelper.createCategory;
import static org.example.util.TestHelper.generateStringOfLength;
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

import java.util.List;
import java.util.stream.Stream;
import org.example.jtsb02.category.dto.CategoryDto;
import org.example.jtsb02.category.entity.Category;
import org.example.jtsb02.category.service.CategoryService;
import org.example.jtsb02.common.security.SecurityConfig;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.service.MemberService;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.service.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(QuestionController.class)
@Import(SecurityConfig.class)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("GET /question/create - Question 생성 폼이동")
    @WithMockUser
    void createQuestionForm() throws Exception {
        //given
        String url = "/question/create";
        Category category = createCategory();
        List<CategoryDto> categories = Stream.of(category).map(CategoryDto::fromCategory).toList();
        when(categoryService.getAllCategories()).thenReturn(categories);

        //when
        ResultActions result = mockMvc.perform(get(url));

        //then
        result.andExpect(status().isOk())
            .andExpect(view().name("question/form/create"))
            .andExpect(model().attributeExists("questionForm"))
            .andExpect(model().attribute("categories", categories));
    }

    @Test
    @DisplayName("POST /question/create - Question 생성 성공")
    @WithMockUser(username = "onlyTest")
    void createQuestion() throws Exception {
        //given
        String url = "/question/create";
        MemberDto member = MemberDto.builder()
            .id(1L)
            .memberId("onlyTest")
            .nickname("onlyTest")
            .email("onlyTest@test.com")
            .build();
        when(memberService.getMemberByMemberId("onlyTest")).thenReturn(member);
        when(questionService.createQuestion(any(QuestionForm.class), any(MemberDto.class))).thenReturn(1L);

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("categoryId", "1")
            .param("subject", "제목")
            .param("content", "내용"));

        //then
        result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/question/detail/1"));
        verify(questionService, times(1)).createQuestion(ArgumentMatchers.any(QuestionForm.class),
            ArgumentMatchers.any(MemberDto.class));
    }

    @Test
    @DisplayName("POST /question/create - Question 생성 실패 - 유효성 검증")
    @WithMockUser
    void createQuestionEmptySubject() throws Exception {
        // Case 1: 제목이 비어있는 경우
        performCreateQuestionRequest("", "내용")
            .andExpect(status().isOk())
            .andExpect(view().name("question/form/create"))
            .andExpect(model().attributeExists("questionForm"));

        // Case 2: 내용이 비어있는 경우
        performCreateQuestionRequest("제목", "")
            .andExpect(status().isOk())
            .andExpect(view().name("question/form/create"))
            .andExpect(model().attributeExists("questionForm"));

        // Case 3: 둘다 비어있는 경우
        performCreateQuestionRequest("", "")
            .andExpect(status().isOk())
            .andExpect(view().name("question/form/create"))
            .andExpect(model().attributeExists("questionForm"));

        // Case 4: 제목이 200자를 넘는 경우
        performCreateQuestionRequest(generateStringOfLength(201), "내용")
            .andExpect(status().isOk())
            .andExpect(view().name("question/form/create"))
            .andExpect(model().attributeExists("questionForm"));

        // Case 5: 내용이 2000자를 넘는 경우
        performCreateQuestionRequest("제목", generateStringOfLength(2001))
            .andExpect(status().isOk())
            .andExpect(view().name("question/form/create"))
            .andExpect(model().attributeExists("questionForm"));

        // questionService.createQuestion 이 호출되지 않아야함.
        verify(questionService, times(0)).createQuestion(ArgumentMatchers.any(QuestionForm.class),
            ArgumentMatchers.any(MemberDto.class));
    }

    private ResultActions performCreateQuestionRequest(String subject, String content) throws Exception {
        return mockMvc.perform(post("/question/create")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("categoryId", "1")
            .param("subject", subject)
            .param("content", content));
    }
}