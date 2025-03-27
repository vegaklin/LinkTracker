package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.exception.LinkNotFoundException;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(MethodArgumentNotValidException ex) {
        log.error("Error processing request parameters", ex);

        ApiErrorResponse response = new ApiErrorResponse(
                "Некорректные параметры запроса",
                "400",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toList()));
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkNotFound(LinkNotFoundException ex) {
        log.warn("Link not found exception", ex);

        ApiErrorResponse response = new ApiErrorResponse(
                "Ссылка не найдена",
                "404",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleChatNotFound(ChatNotFoundException ex) {
        log.warn("Chat not found exception", ex);

        ApiErrorResponse response = new ApiErrorResponse(
                "Чат не найден",
                "404",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
