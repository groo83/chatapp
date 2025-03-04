package com.groo.chatapp.common.exception;

import com.groo.chatapp.common.code.ErrorCode;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public DuplicateResourceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
