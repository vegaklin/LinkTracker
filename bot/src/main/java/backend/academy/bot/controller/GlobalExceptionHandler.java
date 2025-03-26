package backend.academy.bot.controller;

import backend.academy.bot.dto.ApiErrorResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
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
                        .toList());
        return ResponseEntity.badRequest().body(response);
    }
}
