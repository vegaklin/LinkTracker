package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.ApiErrorResponse;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(MethodArgumentNotValidException ex) {
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

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(MissingRequestHeaderException ex) {
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
}
