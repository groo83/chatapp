package com.groo.chatapp.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groo.chatapp.EmbeddedRedisConfig;
import com.groo.chatapp.domain.member.dto.MemberRegReqDto;
import com.groo.chatapp.domain.member.dto.MemberReqDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(EmbeddedRedisConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    private final String domain = "http://localhost:";

    @Test
    public void signupWithJsonData() throws Exception {
        MemberRegReqDto reqDto = new MemberRegReqDto("jy@google.com", "jy", "password");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").value("jy@google.com"))
                .andExpect(jsonPath("$.data.nickname").value("jy"));
    }

    @Test
    public void signupWithJsonDataExistEmail() throws Exception {
        MemberRegReqDto reqDto = new MemberRegReqDto("test", "groo", "1");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 이메일이 존재합니다."));
    }

    @Test
    void singinWithUserRole() {
        String url = domain + randomServerPort + "/api/auth/login";
        MemberReqDto reqDto = new MemberReqDto("test", "1");

        ResponseEntity<Object> response = restTemplate
                .postForEntity(url, reqDto, Object.class);

        // 응답 검증
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

}
