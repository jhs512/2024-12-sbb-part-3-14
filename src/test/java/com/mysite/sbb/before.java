package com.mysite.sbb;

import com.mysite.sbb.qustion.QuestionForm;
import com.mysite.sbb.user.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class before {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    UserRepository userRepository;
    //mockMvc 객체 생성, Spring Security 환경 setup
    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    public MockHttpSession loginSession() throws Exception {
        mockMvc.perform(post("/user/signup_run")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userName", "testuser")
                .param("email", "ㅇ롷ㅇㅎㄹㄹzzzzㅇzsㅎㄶㄹㄴㅇㅎㄴㅇ")
                .param("password", "password")
                .param("pwCheck", "password"));
        String username = "testuser";
        String password = "password";

        //mockMvc.perform(formLogin("/user/login").user(username).password(password))
        //		.andExpect(redirectedUrl("/question/list/1"));
        MockHttpServletRequest request = mockMvc.perform(post("/user/login")
                        .param("username", username)
                        .param("password", password)) // 세션 추가
                .andExpect(redirectedUrl("/question/list/1")).andReturn().getRequest();
        return (MockHttpSession) request.getSession();
    }

    public String insertCategory(MockHttpSession session) throws Exception {
        mockMvc.perform(post("/category/create").session(session)
                .param("content", "게시판"));

        return "게시판";
    }


    public void insertQuestion(MockHttpSession session) throws Exception{
        insertCategory(loginSession());
        QuestionForm questionForm = new QuestionForm("제목","내용");
        mockMvc.perform(post("/question/create/1").session(session)
                        .flashAttr("questionForm", questionForm))
                .andExpect(redirectedUrl("/question/list/1"));
    }

}
