package com.groo.chatapp.config;

import com.groo.chatapp.aop.WebSocketLoggingInterceptor;
import com.groo.chatapp.common.jwt.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketLoggingInterceptor loggingInterceptor;
    private final JwtChannelInterceptor jwtChannelInterceptor;

    private static final String ENDPOINT = "/ws-stomp";
    private static final String STIMPLE_BROKER_TOPIC = "/topic";
    private static final String STIMPLE_BROKER_QUEUE = "/queue";
    private static final String PUBLISH = "/app";
    private static final int MESSAGE_SIZE_LIMIT = 2 * 1024 * 1024; // 메시지 크기 제한
    private static final int BUFFER_SIZE_LIMIT = 2 * 1024 * 1024; // 송신 버퍼 크기
    private static final int SEND_TIME_LIMIT = 20 * 1000;  // 전송 시간 제한: 20초

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(loggingInterceptor, jwtChannelInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(loggingInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(STIMPLE_BROKER_TOPIC, STIMPLE_BROKER_QUEUE); // 메세지 브로커의 prefix 설정
        registry.setApplicationDestinationPrefixes(PUBLISH); // 클라이언트가 보낼 메세지의 prefix 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ENDPOINT)
                .setAllowedOrigins("*"); // CORS 허용
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(MESSAGE_SIZE_LIMIT);
        registration.setSendBufferSizeLimit(BUFFER_SIZE_LIMIT);
        registration.setSendTimeLimit(SEND_TIME_LIMIT);
    }
}
