package com.groo.chatapp.common.dto;

import com.groo.chatapp.common.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final int status;      // HTTP 상태 코드
    private final String errorCode; // (선택) 에러 코드
    private final String message;  // 에러 메시지

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
    }
}
