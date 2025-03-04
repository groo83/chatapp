package com.groo.chatapp.common.exception;

import com.groo.chatapp.common.code.ErrorCode;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
