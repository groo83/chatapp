package com.groo.chatapp.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.groo.chatapp.security.constants.SecurityConstants.AUTHORIZATION_HEADER;
import static com.groo.chatapp.security.constants.SecurityConstants.BEARER_PREFIX;

/**
 * STOMP 인증 정보 유지
 */
@Component
@RequiredArgsConstructor
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) { //CONNECT -> SEND 수정
            String token = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);

            if ((token != null) && token.startsWith(BEARER_PREFIX)) {
                token = token.substring(7);

                Authentication authentication = tokenProvider.getAuthentication(token);
                accessor.setUser(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // todo SecurityContextHolder stomp 유지 가능 여부 : 하위 thread 유지
/*
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
*/

            }
        }
        return message;
    }
}
