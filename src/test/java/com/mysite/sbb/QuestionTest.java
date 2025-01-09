package com.mysite.sbb;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.qustion.QuestionForm;
import com.mysite.sbb.user.UserPasswordForm;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;
import jakarta.validation.constraints.NotEmpty;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

public class QuestionTest extends before{

    @Test
    @Order(1)
    @Commit
    @DisplayName("게시글  창 성공")
    public void 게시글_창_성공() throws Exception{
        insertCategory(loginSession());
        mockMvc.perform(get("/question/list/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @Commit
    @DisplayName("게시글 작성 창 성공")
    public void 게시글_작성_창_성공() throws Exception{
        insertCategory(loginSession());
        QuestionForm questionForm = new QuestionForm("제목","내용");
        mockMvc.perform(get("/question/insert/1").session(loginSession())
                        .flashAttr("questionForm", questionForm))
                .andExpect(status().isOk());
    }
    @Test
    @Order(2)
    @Commit
    @DisplayName("게시글 작성 성공")
    public void 게시글_작성_성공() throws Exception{
        insertCategory(loginSession());
        QuestionForm questionForm = new QuestionForm("제목","내용");
        mockMvc.perform(post("/question/create/1").session(loginSession())
                        .flashAttr("questionForm", questionForm))
                .andExpect(redirectedUrl("/question/list/1"));
    }
    @Test
    @Order(3)
    @Commit
    @DisplayName("게시글 수정 성공")
    public void 게시글_보기_성공() throws Exception{
        insertCategory(loginSession());
        AnswerForm answerForm = new AnswerForm();
        mockMvc.perform(get("/question/detail/1").session(loginSession())
                        .flashAttr("qanswerForm ", answerForm ))
                .andExpect(status().isOk());
    }
    @Test
    @Order(4)
    @Commit
    @DisplayName("게시글 수정 창 성공")
    public void 게시글_수정_창_성공() throws Exception{
        insertCategory(loginSession());
        QuestionForm questionForm = new QuestionForm("제목","내용");
        mockMvc.perform(get("/question/modify/1").session(loginSession())
                        .flashAttr("questionForm", questionForm))
                .andExpect(status().isOk());
    }
    @Test
    @Order(5)
    @Commit
    @DisplayName("게시글 수정 성공")
    public void 게시글_수정_성공() throws Exception{
        insertCategory(loginSession());
        QuestionForm questionForm = new QuestionForm("제목","내용");
        mockMvc.perform(post("/question/modifyset/1").session(loginSession()))
                .andExpect(redirectedUrl("/question/detail/1"));
    }
    @Test
    @Order(6)
    @Commit
    @DisplayName("게시글 추천 성공")
    public void 게시글_추천_성공() throws Exception{
        insertCategory(loginSession());
        QuestionForm questionForm = new QuestionForm("제목","내용");
        mockMvc.perform(get("/question/recommend/1").session(loginSession()))
                .andExpect(redirectedUrl("/question/detail/1"));
    }
    @Test
    @Order(7)
    @Commit
    @DisplayName("게시글 삭제 성공")
    public void 게시글_삭제_성공() throws Exception{
        insertCategory(loginSession());
        QuestionForm questionForm = new QuestionForm("제목","내용");
        mockMvc.perform(get("/question/delete/1").session(loginSession()))
                .andExpect(redirectedUrl("/question/list/1"));
    }

}
