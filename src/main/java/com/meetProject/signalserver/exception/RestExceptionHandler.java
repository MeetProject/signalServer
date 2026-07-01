package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.dto.rest.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException exception) {
        HttpStatus status = switch (exception.getCode()) {
            case ROOM_NOT_FOUND, USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ROOM_FULL, USER_NOT_JOINED -> HttpStatus.CONFLICT;
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(ErrorCode.INTERNAL_ERROR, "요청 처리 중 오류가 발생했습니다."));
    }
}
