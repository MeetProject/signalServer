package com.meetProject.signalserver.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.dto.socket.SignalErrorResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Optional;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class StompExceptionHandler {
    private static final String INVALID_REQUEST_MESSAGE = "유효하지 않은 요청입니다.";

    private final StompMessageSender stompMessageSender;
    private final ObjectMapper objectMapper;

    public StompExceptionHandler(StompMessageSender stompMessageSender, ObjectMapper objectMapper) {
        this.stompMessageSender = stompMessageSender;
        this.objectMapper = objectMapper;
    }

    @MessageExceptionHandler(BusinessException.class)
    public void handleBusiness(BusinessException exception, Message<?> message, Principal principal) {
        sendError(principal, message, exception.getCode(), exception.getMessage());
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidation(MethodArgumentNotValidException exception, Message<?> message, Principal principal) {
        String errorMessage = Optional.ofNullable(exception.getBindingResult())
                .map(BindingResult::getFieldErrors)
                .flatMap(errors -> errors.stream().findFirst())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(INVALID_REQUEST_MESSAGE);
        sendError(principal, message, ErrorCode.INVALID_INPUT, errorMessage);
    }

    @MessageExceptionHandler(MessageConversionException.class)
    public void handleConversion(MessageConversionException exception, Message<?> message, Principal principal) {
        String errorMessage = businessCauseMessage(exception).orElse(INVALID_REQUEST_MESSAGE);
        sendError(principal, message, ErrorCode.INVALID_INPUT, errorMessage);
    }

    @MessageExceptionHandler(Exception.class)
    public void handleUnexpected(Message<?> message, Principal principal) {
        sendError(principal, message, ErrorCode.INTERNAL_ERROR, "요청 처리 중 오류가 발생했습니다.");
    }

    private Optional<String> businessCauseMessage(Throwable exception) {
        for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
            if (cause instanceof BusinessException business) {
                return Optional.of(business.getMessage());
            }
        }
        return Optional.empty();
    }

    private void sendError(Principal principal, Message<?> message, ErrorCode code, String errorMessage) {
        if (principal == null) {
            return;
        }
        String correlationId = extractCorrelationId(message);
        stompMessageSender.sendToUser(principal.getName(), new SignalErrorResponse(correlationId, code, errorMessage));
    }

    private String extractCorrelationId(Message<?> message) {
        try {
            Object payload = message.getPayload();
            byte[] bytes = payload instanceof byte[] raw ? raw : payload.toString().getBytes(StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(bytes);
            JsonNode correlationId = node.get("correlationId");
            return correlationId == null || correlationId.isNull() ? null : correlationId.asText();
        } catch (Exception e) {
            return null;
        }
    }
}
