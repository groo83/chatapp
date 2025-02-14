package com.groo.chatapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groo.chatapp.domain.member.dto.MemberRegReqDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChatAppApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void signupWithJsonData() throws Exception {
		MemberRegReqDto reqDto = new MemberRegReqDto("pjy3696@naver.com", "jiyeong", "password");

		mockMvc.perform(post("/api/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(reqDto)))
						// form 데이터
//						.param("email", "pjy3696@naver.com")
//						.param("password", "1234")
//						.param("nickname", "jiyeong"))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data.email").value("pjy3696@naver.com"))
				.andExpect(jsonPath("$.data.nickname").value("jiyeong"));
	}


}