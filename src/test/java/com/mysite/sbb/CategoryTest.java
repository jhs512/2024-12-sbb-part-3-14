package com.mysite.sbb;

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



@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryTest extends before{
    @Test
    @Order(1)
    @Commit
    @DisplayName("카테고리 작성 성공")
    public void 카테고리_작성_성공() throws Exception{
        mockMvc.perform(post("/category/create").session(loginSession())
                .param("content", "게시판"))
                .andExpect(redirectedUrl("/question/list/1"));
    }
    @Test
    @Order(2)
    @Commit
    @DisplayName("카테고리 작성창 성공")
    public void 카테고리_작성창_성공() throws Exception{
            mockMvc.perform(get("/category/insert").session(loginSession()))
                    .andExpect(status().isOk());
    }
    @Test
    @Order(3)
    @Commit
    @DisplayName("카테고리 작성창 실패")
    public void 카테고리_작성창_실패() throws Exception{
        mockMvc.perform(get("/category/insert"))
                .andExpect(redirectedUrl("/question/list/1"));
    }


/*
    @GetMapping("/insert")
    public String insert(CategoryForm categoryForm, Principal principal){
        if(principal == null)
            return "redirect:/question/list/1";
        return "/category/create_category";
    }
    @PostMapping("/create")
    public String create(@Valid CategoryForm categoryForm, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "redirect:/question/list/1";
        }
        categoryService.create(categoryForm.getContent());
        return "redirect:/question/list/1";
    }*/
}
