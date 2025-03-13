package backend.academy.scrapper.client;

import backend.academy.scrapper.client.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BotClient {

    private final WebClient webClient;

    public Mono<Void> sendUpdate(LinkUpdate update) {
        return webClient.post()
            .uri("/updates")
            .bodyValue(update)
            .retrieve()
            .bodyToMono(Void.class);
    }
}
