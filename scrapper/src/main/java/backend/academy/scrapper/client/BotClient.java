package backend.academy.scrapper.client;

import backend.academy.scrapper.client.dto.LinkUpdate;
import backend.academy.scrapper.configuration.WebClientConfig;
import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.exception.BotClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BotClient {

    private final WebClient botWebClient;

    private static final String UPDATES_ENDPOINT = "/updates";

    public BotClient(@Qualifier(WebClientConfig.BOT_WEB_CLIENT) WebClient botWebClient) {
        this.botWebClient = botWebClient;
    }

    public Mono<Void> sendUpdate(LinkUpdate update) {
        return botWebClient
                .post()
                .uri(UPDATES_ENDPOINT)
                .bodyValue(update)
                .exchangeToMono(response -> handleResponse(response, Void.class));
    }

    private <T> Mono<T> handleResponse(ClientResponse response, Class<T> responseType) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(responseType).doOnSuccess(result -> log.info("Successfully: {}", result));
        } else {
            return response.bodyToMono(ApiErrorResponse.class).map(error -> {
                log.error("Client error: {} (code: {})", error.description(), error.code());
                throw new BotClientException(error);
            });
        }
    }
}
