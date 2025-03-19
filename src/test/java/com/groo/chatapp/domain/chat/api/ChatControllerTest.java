package com.groo.chatapp.domain.chat.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groo.chatapp.EmbeddedRedisConfig;
import com.groo.chatapp.common.dto.DataResponse;
import com.groo.chatapp.config.WebSocketConfig;
import com.groo.chatapp.domain.chat.dto.ChatMessageDto;
import com.groo.chatapp.domain.member.dto.MemberReqDto;
import com.groo.chatapp.domain.member.dto.MemberResDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisConfig.class)
public class ChatControllerTest {

    private WebSocketStompClient stompClient;

    @Autowired
    private WebSocketConfig webSocketConfig;

    @LocalServerPort
    int randomServerPort;

    // 로컬서버 구동없이 테스트 진행
    private String WEBSOCKET_URL; // WebSocket 엔드포인트
    //private String WEBSOCKET_URL = "ws://localhost:8080/ws-stomp"; // 로컬서버 구동 필요
    private Long ROOM_ID = 1L;
    private String SUBSCRIBE_URL = "/topic/" + ROOM_ID + "/messages"; // 메시지를 구독할 경로
    private String SEND_URL = "/app/chat"; // 메시지를 보낼 경로

    private StompSession stompSession;
    private final String domain = "http://localhost:";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() throws Exception {
        // 로그인 API로 JWT 토큰을 발급받음
        String jwtToken = getJwtTokenForUser("test", "1");

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + jwtToken);

        WEBSOCKET_URL = "ws://localhost:" + randomServerPort + "/ws-stomp";
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        CountDownLatch latch = new CountDownLatch(1); // 테스트에서 연결 완료 확인용

        stompSession = stompClient.connectAsync(WEBSOCKET_URL, new WebSocketHttpHeaders(), connectHeaders,
                new StompSessionHandlerAdapter() {}).get(3, TimeUnit.SECONDS);
    }

    @AfterEach
    public void down() throws Exception {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
        if (stompClient != null) {
            stompClient.stop();
        }
    }

    @Test
    public void testHandleMessage() throws Exception {
        // 메시지 전송을 검증할 CountDownLatch (비동기 응답을 기다리기 위한 도구)
        CountDownLatch latch = new CountDownLatch(1);

        // 받은 메시지를 저장할 변수
        final ChatMessageDto[] receivedMessage = new ChatMessageDto[1];

        // WebSocket 메시지 구독
        stompSession.subscribe(SUBSCRIBE_URL, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessage[0] = (ChatMessageDto) payload;
                latch.countDown(); // 메시지를 받으면 latch 감소
            }
        });

        // 테스트용 메시지 전송
        ChatMessageDto testMessage = new ChatMessageDto();
        testMessage.setContent("Hello, WebSocket!");
        testMessage.setRoomId(ROOM_ID);
        testMessage.setType("message");
        testMessage.setSender("groo");
        stompSession.send(SEND_URL, testMessage);

        // latch가 5초 이내에 0이 되면 성공
        assertThat(latch.await(5000, TimeUnit.SECONDS)).isTrue();

        // 받은 메시지가 예상한 값인지 확인
        assertThat(receivedMessage[0]).isNotNull();
        assertThat(receivedMessage[0].getContent()).isEqualTo("Hello, WebSocket!");
    }

    // 로그인 API를 호출하여 JWT 토큰을 받아오는 메서드
    private String getJwtTokenForUser(String username, String password) throws JsonProcessingException {
        String url = domain + randomServerPort + "/api/auth/login";
        MemberReqDto reqDto = new MemberReqDto("test", "1");

        restTemplate.getRestTemplate().getMessageConverters().add(0, new MappingJackson2HttpMessageConverter(objectMapper));

        // Basic Authentication 활용 withBasicAuth 인증
        ResponseEntity<String> responseEntity = restTemplate.withBasicAuth(username, password)
                .exchange(url, HttpMethod.POST, new HttpEntity<>(reqDto), String.class);

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
