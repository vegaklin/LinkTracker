package backend.academy.scrapper.client;

import backend.academy.scrapper.client.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BotClient {

    private final WebClient botWebClient;

    public Mono<Void> sendUpdate(LinkUpdate update) {
        return botWebClient.post()
            .uri("http://localhost:8080/updates")
            .bodyValue(update)
            .retrieve()
            .bodyToMono(Void.class);
    }
}
