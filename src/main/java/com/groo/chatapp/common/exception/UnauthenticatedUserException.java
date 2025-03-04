package com.groo.chatapp.common.exception;

import com.groo.chatapp.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class UnauthenticatedUserException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthenticatedUserException() {
        super(ErrorCode.UNAUTHORIZED.getMessage());
        this.errorCode = ErrorCode.UNAUTHORIZED;
    }

    public UnauthenticatedUserException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public UnauthenticatedUserException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
