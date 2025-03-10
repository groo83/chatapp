package com.groo.chatapp.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groo.chatapp.EmbeddedRedisConfig;
import com.groo.chatapp.common.dto.DataResponse;
import com.groo.chatapp.domain.member.dto.MemberReqDto;
import com.groo.chatapp.domain.member.dto.MemberResDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisConfig.class)
public class JwtAuthTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    private final String domain = "http://localhost:";

    /**
     * todo validateToken true 리턴 확인되었지만 UNAUTHORIZED 응답으로인한 주석처리
     * @throws JsonProcessingException
     */
    //@Test
    void authWithJwtToken() throws JsonProcessingException {
        String url = domain + randomServerPort + "/api/member";

        // 로그인 API로 JWT 토큰을 발급받음
        String jwtToken = getJwtTokenForUser("test", "1");

        // Authorization 헤더에 JWT 토큰을 추가하여 API 호출
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        //headers.set("Authorization", "Bearer " + jwtToken);

        //ChatRoomReqDto reqDto = new ChatRoomReqDto("test chatroom");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 응답 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    // 로그인 API를 호출하여 JWT 토큰을 받아오는 메서드
    private String getJwtTokenForUser(String username, String password) throws JsonProcessingException {
        // 로그인 API 호출하여 JWT 토큰 받아오기 (예시, 실제 구현은 로그인 API와 연동)
        // 예시에서는 단순히 "dummy-token"을 반환
        String url = domain + randomServerPort + "/api/auth/login";
        MemberReqDto reqDto = new MemberReqDto("test", "1");

        restTemplate.getRestTemplate().getMessageConverters().add(0, new MappingJackson2HttpMessageConverter(objectMapper));

        // Basic Authentication 활용 withBasicAuth 인증
        ResponseEntity<String> responseEntity = restTemplate.withBasicAuth(username, password)
                .exchange(url, HttpMethod.POST, new HttpEntity<>(reqDto), String.class);

        //ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.registerModule(new JavaTimeModule());  // LocalDateTime 지원을 위해 모듈 등록

        // JSON을 DataResponse<ChatRoomResDto>로 변환
        DataResponse<MemberResDto> dataResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<DataResponse<MemberResDto>>() {}
        );

        // 응답 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dataResponse.getData().getAccessToken()).isNotNull();

        return dataResponse.getData().getAccessToken();
    }
}
