package backend.academy.bot.exception;

import backend.academy.bot.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ScrapperClientException extends RuntimeException {

    private final ApiErrorResponse apiErrorResponse;

    public ScrapperClientException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse.description());
        this.apiErrorResponse = apiErrorResponse;
    }
}
