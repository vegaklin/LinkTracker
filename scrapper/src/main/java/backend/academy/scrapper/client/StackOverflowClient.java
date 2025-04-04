package backend.academy.scrapper.client;

import backend.academy.scrapper.client.dto.StackOverflowResponse;
import backend.academy.scrapper.client.dto.StackOverflowResponseWrapper;
import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.exception.ApiClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class StackOverflowClient {

    private final ScrapperConfig scrapperConfig;

    private final WebClient stackOverflowWebClient;

    public Mono<StackOverflowResponse> getQuestion(Long questionId) {
        return stackOverflowWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/2.3/questions/{questionId}")
                        .queryParam("site", "stackoverflow")
                        .queryParam("key", scrapperConfig.stackOverflow().key())
//                        .queryParam(
//                                "access_token", scrapperConfig.stackOverflow().accessToken())
                        .queryParam("filter", "default")
                        .build(questionId))
                .exchangeToMono(response -> handleResponse(response, questionId));
    }

    private Mono<StackOverflowResponse> handleResponse(ClientResponse response, Long questionId) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(StackOverflowResponseWrapper.class)
                    .map(wrapper -> {
                        if (wrapper.items().isEmpty()) {
                            log.warn("No question found for ID: {}", questionId);
                            throw new ApiClientException("Empty items list");
                        }
                        return wrapper.items().getFirst();
                    })
                    .doOnSuccess(result -> log.info("Fetched SO question {}: {}", questionId, result));
        } else {
            return response.bodyToMono(String.class).map(error -> {
                int statusCode = response.statusCode().value();
                log.error("StackOverflow API error for question {}: status={}: {}", questionId, statusCode, error);
                throw new ApiClientException(error);
            });
        }
    }
}
