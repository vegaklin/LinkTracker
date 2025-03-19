package backend.academy.bot.exception;

import backend.academy.bot.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ScrapperClientException extends RuntimeException {

    private final ApiErrorResponse errorResponse;

    public ScrapperClientException(ApiErrorResponse errorResponse) {
        super(errorResponse.description());
        this.errorResponse = errorResponse;
    }

}
