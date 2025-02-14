package com.groo.chatapp.config;


/**
 * as-is) STOMP websocket 으로 파일 전송할때 사용.
 * to-be) HTTP form 데이터로 전송하므로 주석처리
 */
//@Configuration
public class JacksonConfig {
/*
    @Bean
    public ObjectMapper objectMapper() {
        StreamReadConstraints constraints = StreamReadConstraints.builder()
                .maxStringLength(500_000_000) // 500MB로 제한 증가
                .build();

        return new ObjectMapper(JsonFactory.builder()
                .streamReadConstraints(constraints)
                .build());
    }*/
}
