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
class LoginTest extends before{


	@Test
	@Order(1)
	@DisplayName("회원가입 창 성공")
	void 회원가입_창_성공() throws Exception {

		mockMvc.perform(get("/user/signup"))
				.andExpect(status().isOk());

	}

	@Test
	@Order(2)
	@Commit
	@DisplayName("회원가입 성공")
	void 회원가입_성공() throws Exception{
		mockMvc.perform(post("/user/signup_run")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("userName", "testusser")
						.param("email", "ㅇ롷ㅇㅎㄹㄹzzzzㅇzsㅎㄶㄹㄴㅇsㅎㄴㅇ")
						.param("password", "password")
						.param("pwCheck", "password"))
				.andExpect(redirectedUrl("/question/list/1"));
	}

	@Test
	@Order(3)
	@DisplayName("로그인 창 성공")
	void login_window_success() throws Exception {

		mockMvc.perform(get("/user/login"))
				.andExpect(status().isOk());
	}
	@Test
	@Order(4)
	@DisplayName("로그인 성공")
	public void 로그인_성공() throws Exception{
		//회원가입

		String username = "testuser";
		String password = "password";

		//mockMvc.perform(formLogin("/user/login").user(username).password(password))
		//		.andExpect(redirectedUrl("/question/list/1"));
		mockMvc.perform(post("/user/login")
						.param("username", username)
						.param("password", password)) // 세션 추가
				.andExpect(redirectedUrl("/question/list/1"));
	}
	@Test
	@DisplayName("로그인 실패")
	@Order(5)
	public void 로그인_실패() throws Exception{
		String username = "존재하지 않는 아이디";
		String password = "123";
		mockMvc.perform(post("/user/login")
						.param("username", username)
						.param("password", password)) // 세션 추가
				.andExpect(redirectedUrl("/user/login?error"));
	}

	@Test
	@Order(6)
	@DisplayName("패스워드_수정_창_성공")
	public void 패스워드_수정_창_성공() throws Exception{
		UserPasswordForm userPasswordForm = new UserPasswordForm();
		mockMvc.perform(get("/user/user_profile").session(loginSession())
				.flashAttr("userPasswordForm", userPasswordForm))
				.andExpect(status().isOk());
	}

	@Test
	@Order(7)
	@DisplayName("패스워드_수정_성공")
	public void 패스워드_수정_성공() throws Exception{

		mockMvc.perform(post("/user/change_password").session(loginSession())
				.param("password", "password")
				.param("newpassword", "password2")
				.param("newpasswordcheck", "password2"))
				.andExpect(status().isOk());
		String username = "testuser";
		String password = "password2";

		//mockMvc.perform(formLogin("/user/login").user(username).password(password))
		//		.andExpect(redirectedUrl("/question/list/1"));
		mockMvc.perform(post("/user/login")
						.param("username", username)
						.param("password", password)) // 세션 추가
				.andExpect(redirectedUrl("/question/list/1"));
	}
	@Test
	@Order(8)
	@Commit
	@DisplayName("회원가입 반복 성공")
	void 회원가입_반복_성공() throws Exception{
		for(int i = 0;i< 100; i++){
			mockMvc.perform(post("/user/signup_run")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.param("userName", "testuser"+i)
							.param("email", "ㅇ롷ㅇㅎㄹㄹzzzzㅇzsㅎㄶㄹㄴㅇㅎㄴㅇ"+i)
							.param("password", "password")
							.param("pwCheck", "password"));
		}
		assertThat(userRepository.count()).isEqualTo(102);
	}

}