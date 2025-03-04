package com.groo.chatapp.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket Handshake 시 인증 유지
 * jwt 헤더로 전달 불가, 쿼리파라미터로 보내야해서 인증 생략.
 *  > channerInterceptor CONNECT 단계에서 인증하도록 대체.
 * endpoint 미설정.
 */
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        /*
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String token = httpRequest.getParameter("token");
            if (token != null && tokenProvider.validateToken(token)) {
                // JWT 토큰을 검증한 후 Authentication 객체 생성
                Authentication authentication = tokenProvider.getAuthentication(token);

                // WebSocket 세션에 저장 (STOMP에서 인증 정보를 가져올 때 사용)
                //attributes.put("authentication", authentication);
                //SecurityContext context = SecurityContextHolder.createEmptyContext();
                //context.setAuthentication(authentication);
                //SecurityContextHolder.setContext(context);

                //attributes.put("SPRING_SECURITY_CONTEXT", context); // JwtChannelInterceptor 에서 accessor.getSessionAttributes().get("SPRING_SECURITY_CONTEXT"); 이렇게 가겨올 수 있음.

            }
        }*/
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
