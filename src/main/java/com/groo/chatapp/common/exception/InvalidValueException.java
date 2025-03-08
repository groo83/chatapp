package com.groo.chatapp.common.exception;

import com.groo.chatapp.common.code.ErrorCode;

public class InvalidValueException extends BusinessException {
    public InvalidValueException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
