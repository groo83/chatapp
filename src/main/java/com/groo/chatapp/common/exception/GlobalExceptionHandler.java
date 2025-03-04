package com.groo.chatapp.common.exception;

import com.groo.chatapp.common.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessExceptionException(BusinessException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticatedUserException(UnauthenticatedUserException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }


}
