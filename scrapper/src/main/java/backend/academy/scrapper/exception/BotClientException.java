package backend.academy.scrapper.exception;

import backend.academy.scrapper.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class BotClientException extends RuntimeException {

    private final ApiErrorResponse apiErrorResponse;

    public BotClientException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse.description());
        this.apiErrorResponse = apiErrorResponse;
    }
}
