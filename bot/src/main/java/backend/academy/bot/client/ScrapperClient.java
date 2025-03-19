package backend.academy.bot.client;

import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.dto.ApiErrorResponse;
import backend.academy.bot.exception.ScrapperClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.ClientResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScrapperClient {

    private final WebClient scrapperWebClient;

    public Mono<Void> registerChat(Long chatId) {
        return scrapperWebClient.post()
            .uri("/tg-chat/{id}", chatId)
            .exchangeToMono(response -> handleResponse(response, Void.class));
    }

    public Mono<Void> deleteChat(Long chatId) {
        return scrapperWebClient.delete()
            .uri("/tg-chat/{id}", chatId)
            .exchangeToMono(response -> handleResponse(response, Void.class));
    }

    public Mono<ListLinksResponse> getAllLinks(Long chatId) {
        return scrapperWebClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .exchangeToMono(response -> handleResponse(response, ListLinksResponse.class));
    }

    public Mono<LinkResponse> addLink(Long chatId, AddLinkRequest request) {
        return scrapperWebClient.post()
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .bodyValue(request)
            .exchangeToMono(response -> handleResponse(response, LinkResponse.class));
    }

    public Mono<LinkResponse> removeLink(Long chatId, RemoveLinkRequest request) {
        return scrapperWebClient.method(HttpMethod.DELETE)
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .bodyValue(request)
            .exchangeToMono(response -> handleResponse(response, LinkResponse.class));
    }

    private <T> Mono<T> handleResponse(ClientResponse response, Class<T> responseType) {
        if (response.statusCode().is2xxSuccessful()) {
            return response
                .bodyToMono(responseType)
                .doOnSuccess(result -> log.debug("Successfully: {}", result));
        } else {
            return response
                .bodyToMono(ApiErrorResponse.class)
                .map(error -> {
                    log.warn("Client error: {} (code: {})", error.description(), error.code());
                    throw new ScrapperClientException(error);
                });
        }
    }
}
