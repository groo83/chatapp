package com.groo.chatapp.domain.chat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.groo.chatapp.common.dto.DataResponse;
import com.groo.chatapp.domain.chat.dto.ChatRoomReqDto;
import com.groo.chatapp.domain.chat.dto.ChatRoomResDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc // resttemplate 대신 사용
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    private final String domain = "http://localhost:";

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    // email이 "test"인 사용자의 Security Context가 설정됨
    @WithUserDetails(value = "test", userDetailsServiceBeanName = "customUserDetailsService")
    void createChatRoom() throws Exception {
        // given
        ChatRoomReqDto reqDto = new ChatRoomReqDto("test chatroom");

        mockMvc.perform(post("/api/chatroom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.chatRoomId").value(2L))
                .andExpect(jsonPath("$.data.name").value("test chatroom"));
    }

    /**
     * RestTemplate + Basic Authentication 인증
     * @throws Exception
     */
    @Test
    void createChatRoomByRestTemplate() throws Exception {
        String url = domain + randomServerPort + "/api/chatroom";
        ChatRoomReqDto reqDto = new ChatRoomReqDto("test chatroom");

        restTemplate.getRestTemplate().getMessageConverters().add(0, new MappingJackson2HttpMessageConverter(objectMapper));

        // Basic Authentication 활용 withBasicAuth 인증
        ResponseEntity<String> responseEntity = restTemplate.withBasicAuth("test", "1")
                .exchange(url, HttpMethod.POST, new HttpEntity<>(reqDto), String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // LocalDateTime 지원을 위해 모듈 등록

        // JSON을 DataResponse<ChatRoomResDto>로 변환
        DataResponse<ChatRoomResDto> dataResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<DataResponse<ChatRoomResDto>>() {}
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(dataResponse.getData().getChatRoomId()).isEqualTo(2L);
        assertThat(dataResponse.getData().getName()).isEqualTo("test chatroom");
    }

}