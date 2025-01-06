package org.example.jtsb02.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.example.jtsb02.common.security.SecurityConfig;
import org.example.jtsb02.member.form.MemberForm;
import org.example.jtsb02.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberController.class)
@Import(SecurityConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("GET /member/signup - 회원가입 폼 이동")
    void signupForm() throws Exception {
        //given
        String url = "/member/signup";

        //when
        ResultActions result = mockMvc.perform(get(url));

        //then
        result.andExpect(status().isOk())
            .andExpect(view().name("member/signup"))
            .andExpect(model().attributeExists("memberForm"));
    }

    @Test
    @DisplayName("POST /member/signup - 회원가입 성공")
    void signup() throws Exception {
        //given
        String url = "/member/signup";

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("memberId", "testId")
            .param("nickname", "testNickName")
            .param("password", "testPassword")
            .param("confirmPassword", "testPassword")
            .param("email", "testEmail@gmail.com")
        );

        //then
        result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
        verify(memberService).createMember(any(MemberForm.class));
    }

    @Test
    @DisplayName("POST /member/signup - 회원가입 실패 - 폼 유효성 검증 실패")
    void signupValidForm() throws Exception {
        //given
        String url = "/member/signup";

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("memberId", "")
            .param("nickname", "")
            .param("password", "testPassword")
            .param("confirmPassword", "testPassword")
            .param("email", "testEmail@gmail.com")
        );

        //then
        result.andExpect(status().isOk())
            .andExpect(view().name("member/signup"))
            .andExpect(model().attributeExists("memberForm"));
        verify(memberService, times(0)).createMember(ArgumentMatchers.any(MemberForm.class));
    }

    @Test
    @DisplayName("POST /member/signup - 회원가입 실패 - 비밀번호 확인이 일치하지 않을 경우")
    void signupNotMatchPassword() throws Exception {
        //given
        String url = "/member/signup";

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(csrf())
            .param("memberId", "testId")
            .param("nickname", "testNickName")
            .param("password", "testPassword")
            .param("confirmPassword", "testPassword12")
            .param("email", "testEmail@gmail.com")
        );

        //then
        result.andExpect(status().isOk())
            .andExpect(view().name("member/signup"))
            .andExpect(model().attributeExists("memberForm"));
        verify(memberService, times(0)).createMember(ArgumentMatchers.any(MemberForm.class));
    }

//    @Test
//    @DisplayName("POST /member/signup - 회원가입 실패 - 이미 등록된 회원이 있을 경우")
//    void signupAlreadyExistMember() throws Exception {
//        //given
//        String url = "/member/signup";
//
//        //when
//        ResultActions result = mockMvc.perform(post(url)
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//            .with(csrf())
//            .param("memberId", "testId")
//            .param("nickname", "testNickName")
//            .param("password", "testPassword")
//            .param("confirmPassword", "testPassword")
//            .param("email", "testEmail@gmail.com")
//        );
//
//        //then
//        result.andExpect(status().isOk())
//            .andExpect(view().name("member/signup"))
//            .andExpect(model().attributeExists("memberForm"));
//        verify(memberService, times(0)).createMember(ArgumentMatchers.any(MemberForm.class));
//    }
}