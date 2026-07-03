package com.meetProject.signalserver.exception;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.dto.socket.SignalErrorResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
public class StompExceptionHandlerTest {
    @Mock
    StompMessageSender sender;

    StompExceptionHandler handler;

    private final Principal principal = () -> "u1";

    @BeforeEach
    void setUp() {
        handler = new StompExceptionHandler(sender, new ObjectMapper());
    }

    private Message<byte[]> message(String payload) {
        return MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
    }

    @Test
    @DisplayName("비즈니스 예외는 correlationId와 에러코드를 사용자에게 보낸다")
    void sendsBusinessError() {
        handler.handleBusiness(new RoomNotFoundException(), message("{\"correlationId\":\"cid-1\"}"), principal);

        verify(sender).sendToUser("u1",
                new SignalErrorResponse("cid-1", ErrorCode.ROOM_NOT_FOUND, "존재하지 않는 방 입니다."));
    }

    @Test
    @DisplayName("예상치 못한 예외는 INTERNAL_ERROR를 보낸다")
    void sendsInternalError() {
        handler.handleUnexpected(message("{\"correlationId\":\"cid-1\"}"), principal);

        verify(sender).sendToUser("u1",
                new SignalErrorResponse("cid-1", ErrorCode.INTERNAL_ERROR, "요청 처리 중 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("principal이 없으면 아무것도 보내지 않는다")
    void skipsWithoutPrincipal() {
        handler.handleBusiness(new RoomNotFoundException(), message("{}"), null);

        verifyNoInteractions(sender);
    }

    @Test
    @DisplayName("correlationId가 없으면 null로 보낸다")
    void sendsNullCorrelationIdWhenMissing() {
        handler.handleBusiness(new RoomFullException(), message("{}"), principal);

        verify(sender).sendToUser("u1",
                new SignalErrorResponse(null, ErrorCode.ROOM_FULL, "방 인원이 가득 찼습니다."));
    }

    @Test
    @DisplayName("페이로드가 JSON이 아니어도 null correlationId로 보낸다")
    void toleratesInvalidPayload() {
        handler.handleBusiness(new RoomFullException(), message("not-json"), principal);

        verify(sender).sendToUser("u1",
                new SignalErrorResponse(null, ErrorCode.ROOM_FULL, "방 인원이 가득 찼습니다."));
    }

    @Test
    @DisplayName("검증 실패는 INVALID_INPUT과 필드 메시지를 보낸다")
    void sendsValidationError() throws Exception {
        Message<byte[]> message = message("{\"correlationId\":\"cid-1\"}");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "joinRequest");
        bindingResult.addError(new FieldError("joinRequest", "roomId", "방 ID는 필수입니다."));
        MethodParameter parameter = new MethodParameter(
                getClass().getDeclaredMethod("sendsValidationError"), -1);

        handler.handleValidation(new MethodArgumentNotValidException(message, parameter, bindingResult),
                message, principal);

        verify(sender).sendToUser("u1",
                new SignalErrorResponse("cid-1", ErrorCode.INVALID_INPUT, "방 ID는 필수입니다."));
    }

    @Test
    @DisplayName("역직렬화 중 도메인 예외가 나면 INVALID_INPUT과 도메인 메시지를 보낸다")
    void sendsConversionErrorWithBusinessCause() {
        Message<byte[]> message = message("{\"correlationId\":\"cid-1\"}");
        MessageConversionException exception = new MessageConversionException(message, "convert fail",
                new RuntimeException(new InvalidInputException("유효하지 않은 미디어 옵션입니다.")));

        handler.handleConversion(exception, message, principal);

        verify(sender).sendToUser("u1",
                new SignalErrorResponse("cid-1", ErrorCode.INVALID_INPUT, "유효하지 않은 미디어 옵션입니다."));
    }

    @Test
    @DisplayName("도메인 원인이 없는 역직렬화 실패는 기본 메시지를 보낸다")
    void sendsConversionErrorWithDefaultMessage() {
        Message<byte[]> message = message("not-json");

        handler.handleConversion(new MessageConversionException("broken"), message, principal);

        verify(sender).sendToUser("u1",
                new SignalErrorResponse(null, ErrorCode.INVALID_INPUT, "유효하지 않은 요청입니다."));
    }
}
