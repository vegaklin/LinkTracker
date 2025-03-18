package backend.academy.bot.client;

import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.dto.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ScrapperClient {

    private final WebClient scrapperWebClient;

    public Mono<Void> registerChat(Long chatId) {
        return scrapperWebClient.post()
            .uri("/tg-chat/{id}", chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Void> deleteChat(Long chatId) {
        return scrapperWebClient.delete()
            .uri("/tg-chat/{id}", chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<ListLinksResponse> getAllLinks(Long chatId) {
        return scrapperWebClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .retrieve()
            .bodyToMono(ListLinksResponse.class);
    }

    public Mono<LinkResponse> addLink(Long chatId, AddLinkRequest request) {
        return scrapperWebClient.post()
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .bodyValue(request)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }

    public Mono<LinkResponse> removeLink(Long chatId, RemoveLinkRequest request) {
        return scrapperWebClient.method(HttpMethod.DELETE)
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }
}
