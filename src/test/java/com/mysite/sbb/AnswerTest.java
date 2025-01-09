package com.mysite.sbb;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.qustion.QuestionRepository;
import com.mysite.sbb.qustion.QuestionService;
import com.mysite.sbb.user.UserPasswordForm;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;
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

public class AnswerTest extends before{
    @Autowired
    QuestionRepository questionRepository;

    @Test
    @Order(1)
    @Commit
    @DisplayName("댓글 작성 성공")
    public void 댓글_작성_성공() throws Exception{
        insertQuestion(loginSession());
        int id = questionRepository.findTopByOrderByCreateDateDesc().getId();
        AnswerForm answerForm = new AnswerForm("내용");
        mockMvc.perform(post("/answer/create/"+id).session(loginSession())
                .flashAttr("answerForm", answerForm))
                .andExpect(redirectedUrl("/question/detail/"+id));
    }
    @Test
    @Order(2)
    @Commit
    @DisplayName("댓글 수정 창 성공")
    public void 댓글_수정_창_성공() throws Exception{
        AnswerForm answerForm = new AnswerForm("내용");
        mockMvc.perform(get("/answer/modify/1").session(loginSession())
                        .flashAttr("answerForm", answerForm))
                .andExpect(status().isOk());
    }
    @Test
    @Order(3)
    @Commit
    @DisplayName("댓글 수정 성공")
    public void 댓글_수정_성공() throws Exception{
        int id = questionRepository.findTopByOrderByCreateDateDesc().getId();
        AnswerForm answerForm = new AnswerForm("내용");
        mockMvc.perform(post("/answer/modifyset/1").session(loginSession())
                        .flashAttr("answerForm", answerForm))
                .andExpect(redirectedUrl("/question/detail/"+id+"#answer_1"));
    }
    @Test
    @Order(4)
    @Commit
    @DisplayName("댓글 추천 성공")
    public void 댓글_추천_성공() throws Exception{
        int id = questionRepository.findTopByOrderByCreateDateDesc().getId();
        AnswerForm answerForm = new AnswerForm("내용");
        mockMvc.perform(get("/answer/recommend/1").session(loginSession()))
                .andExpect(redirectedUrl("/question/detail/"+id));
    }
    @Test
    @Order(5)
    @Commit
    @DisplayName("답글 작성 성공")
    public void 답글_작성_성공() throws Exception{
        int id = questionRepository.findTopByOrderByCreateDateDesc().getId();
        AnswerForm answerForm = new AnswerForm("내용");
        mockMvc.perform(post("/answer/comment_create/1").session(loginSession())
                .flashAttr("answerForm", answerForm))
                .andExpect(redirectedUrl("/question/detail/"+id));
    }
    @Test
    @Order(6)
    @Commit
    @DisplayName("답글 작성 성공")
    public void 댓글_삭제_성공() throws Exception{
        int id = questionRepository.findTopByOrderByCreateDateDesc().getId();

        AnswerForm answerForm = new AnswerForm("내용");
        mockMvc.perform(get("/answer/delete/1").session(loginSession()))
                .andExpect(redirectedUrl("/question/detail/"+id));
    }

}
