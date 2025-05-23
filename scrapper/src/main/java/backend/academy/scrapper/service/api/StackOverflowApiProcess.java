package backend.academy.scrapper.service.api;

import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.client.dto.ApiAnswer;
import backend.academy.scrapper.client.dto.StackOverflowResponse;
import backend.academy.scrapper.exception.ApiClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StackOverflowApiProcess implements ApiProcess {

    private final StackOverflowClient stackOverflowClient;

    @Override
    public boolean isApiUrl(String url) {
        return url.contains("stackoverflow.com");
    }

    @Override
    public ApiAnswer checkUpdate(String url) {
        log.info("Processing update for stackoverflow url {}", url);

        String[] parts = url.split("/");
        if (parts.length < 5) {
            log.warn("Invalid StackOverflow URL format: {}", url);
            return null;
        }

        try {
            Long questionId = Long.parseLong(parts[4]);
            StackOverflowResponse stackOverflowResponse =
                    stackOverflowClient.getQuestion(questionId).block();
            if (stackOverflowResponse != null) {
                return new ApiAnswer(
                        stackOverflowResponse.toMessage(), stackOverflowResponse.getLastActivityDateAsOffsetDateTime());
            }
        } catch (ApiClientException e) {
            log.error("Error parsing URL {}: ", url, e);
        } catch (NumberFormatException e) {
            log.error("Error parsing Long {}: ", parts[4], e);
        }
        return null;
    }
}
