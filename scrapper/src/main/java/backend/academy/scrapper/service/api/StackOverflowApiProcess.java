package backend.academy.scrapper.service.api;

import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.client.dto.StackOverflowResponse;
import backend.academy.scrapper.exception.ApiClientException;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class StackOverflowApiProcess implements ApiProcess {

    private final StackOverflowClient stackOverflowClient;

    public boolean isApiUrl(String url) {
        return url.contains("stackoverflow.com");
    }

    public Mono<OffsetDateTime> checkUpdate(String url) {
        String[] parts = url.split("/");
        if (parts.length < 5) {
            log.warn("Invalid StackOverflow URL format: {}", url);
            return Mono.empty();
        }

        try {
            Long questionId = Long.parseLong(parts[4]);
            return stackOverflowClient
                    .getQuestion(questionId)
                    .map(StackOverflowResponse::getLastActivityDateAsOffsetDateTime);
        } catch (ApiClientException e) {
            log.error("Error parsing URL {}: ", url, e);
        } catch (NumberFormatException e) {
            log.error("Error parsing Long {}: ", parts[4], e);
        }
        return Mono.empty();
    }
}
