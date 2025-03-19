package backend.academy.scrapper.service;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.dto.LinkUpdate;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class LinkService {

    private final LinkRepository linkRepository;
    private final ChatRepository chatRepository;
    private final GitHubClient gitHubClient;
    //    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    // Store last known update times
    private final Map<String, Instant> lastUpdateTimes = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 2000) // Run every minute
    public void checkUpdates() {
        System.out.println("Scheduled run");
        chatRepository.getChatIds().forEach(chatId -> {
            List<LinkResponse> links = linkRepository.findAllLinksByChatId(chatId);

            links.forEach(link -> {
                checkLinkUpdate(link, chatId).subscribe(hasUpdate -> {
                    if (hasUpdate) {
                        System.out.println("hasUpdate");
                        sendUpdateNotification(chatId, link);
                    }
                });
            });
        });
    }

    private Mono<Boolean> checkLinkUpdate(LinkResponse link, Long chatId) {
        URI uri = URI.create(link.url());

        if (uri.getHost().contains("github.com")) {
            return checkGitHubUpdate(link);
        }
        //        else if (uri.getHost().contains("stackoverflow.com")) {
        //            return checkStackOverflowUpdate(link);
        //        }
        return Mono.just(false);
    }

    private Mono<Boolean> checkGitHubUpdate(LinkResponse link) {
        String[] parts = link.url().split("/");
        String owner = parts[3];
        String repo = parts[4];

        return gitHubClient
                .getRepository(owner, repo)
                .map(repoResponse -> {
                    Instant lastUpdate = Instant.parse(repoResponse.updatedAt());
                    Instant previousUpdate = lastUpdateTimes.getOrDefault(link.url(), Instant.EPOCH);
                    //                System.out.println(previousUpdate);
                    lastUpdateTimes.put(link.url(), lastUpdate);
                    return lastUpdate.isAfter(previousUpdate);
                })
                .onErrorResume(e -> Mono.just(false));
    }

    //    private Mono<Boolean> checkStackOverflowUpdate(LinkResponse link) {
    //        String questionId = link.url().split("/")[4];
    //
    //        return stackOverflowClient.getQuestion(Long.valueOf(questionId))
    //            .map(response -> {
    //                if (response.items().isEmpty()) return false;
    //
    //                Instant lastUpdate = Instant.ofEpochSecond(response.items().get(0).lastActivityDate());
    //                Instant previousUpdate = lastUpdateTimes.getOrDefault(link.url(), Instant.EPOCH);
    //
    //                lastUpdateTimes.put(link.url(), lastUpdate);
    //                return lastUpdate.isAfter(previousUpdate);
    //            })
    //            .onErrorResume(e -> Mono.just(false));
    //    }

    private void sendUpdateNotification(Long chatId, LinkResponse link) {
        LinkUpdate update = new LinkUpdate(link.id(), link.url(), "New update available", List.of(chatId));

        botClient.sendUpdate(update).subscribe();
    }
}
