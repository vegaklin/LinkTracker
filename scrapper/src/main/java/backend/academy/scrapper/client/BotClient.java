package backend.academy.scrapper.client;

import backend.academy.scrapper.client.dto.LinkUpdate;
import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.exception.BotClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotClient {

    private final WebClient botWebClient;

    public Mono<Void> sendUpdate(LinkUpdate update) {
        return botWebClient
                .post()
                .uri("/updates")
                .bodyValue(update)
            .exchangeToMono(response -> handleResponse(response, Void.class));
    }

    private <T> Mono<T> handleResponse(ClientResponse response, Class<T> responseType) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(responseType).doOnSuccess(result -> log.debug("Successfully: {}", result));
        } else {
            return response.bodyToMono(ApiErrorResponse.class).map(error -> {
                log.warn("Client error: {} (code: {})", error.description(), error.code());
                throw new BotClientException(error);
            });
        }
    }
}
