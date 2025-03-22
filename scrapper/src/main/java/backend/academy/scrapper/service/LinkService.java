package backend.academy.scrapper.service;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.dto.LinkUpdate;
import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.exception.BotClientException;
import backend.academy.scrapper.repository.chat.ChatLinksRepository;
import backend.academy.scrapper.repository.chat.ChatRepository;
import backend.academy.scrapper.repository.link.LinkRepository;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import jdk.dynalink.linker.LinkRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import backend.academy.scrapper.client.GitHubRepoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class LinkService {

    private final LinkRepository linkRepository;
    private final ChatLinksRepository chatLinksRepository;
    private final ChatRepository chatRepository;
    private final GitHubClient gitHubClient;
    // private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    private final Map<Long, OffsetDateTime> lastUpdates = new HashMap<>();

    @Scheduled(fixedRate = 6000) // Check every 6 seconds for testing (adjust as needed)
    public void checkForUpdates() {
        log.info("Starting scheduled link update check");

        // Получаем все ссылки из LinkRepository
        linkRepository.getLinks().forEach((linkId, value) -> {
            String url = value.link();
            checkLinkUpdate(linkId, url);
        });
    }

    private void checkLinkUpdate(Long linkId, String url) {
        Mono<OffsetDateTime> updateMono = isGitHubUrl(url) ? checkGitHubUpdate(url)
//            : isStackOverflowUrl(url) ? checkStackOverflowUpdate(url)
            : Mono.empty();

        OffsetDateTime lastUpdate = updateMono.block();
        if (lastUpdate != null) {
            OffsetDateTime previousUpdate = lastUpdates.getOrDefault(linkId, OffsetDateTime.MIN);
            if (lastUpdate.isAfter(previousUpdate)) {
                log.info("Update detected for linkId: {}, url: {}", linkId, url);
                lastUpdates.put(linkId, lastUpdate);
                notifyChatsForLink(linkId, url);
            }
        } else {
            log.warn("No update time received for linkId: {}", linkId);
        }
    }

    private void notifyChatsForLink(Long linkId, String url) {
        // Находим все чаты, связанные с этой ссылкой
        Set<Long> chatIds = chatRepository.getChatIds().stream()
            .filter(chatId -> chatLinksRepository.getLinksForChat(chatId).contains(linkId))
            .collect(Collectors.toSet());

        if (!chatIds.isEmpty()) {
            LinkUpdate update = new LinkUpdate(linkId, url, "New update detected", new ArrayList<>(chatIds));
            botClient.sendUpdate(update)
                .subscribe(
                    null,
                    error -> log.error("Failed to send update for link {} to chats {}: {}",
                        url, chatIds, error.getMessage()),
                    () -> log.info("Update notification sent for linkId: {} to chats: {}", linkId, chatIds)
                );
        } else {
            log.info("No chats found for linkId: {}", linkId);
        }
    }

    private Mono<OffsetDateTime> checkGitHubUpdate(String url) {
        String[] parts = url.split("/");
        String owner = parts[3];
        String repo = parts[4];
        return gitHubClient.getRepository(owner, repo)
            .map(GitHubRepoResponse::updatedAt)
            .doOnError(e -> log.error("GitHub API error for {}: {}", url, e.getMessage()));
    }

//    private Mono<OffsetDateTime> checkStackOverflowUpdate(String url) {
//        Long questionId = Long.parseLong(url.split("/")[4]);
//        return stackOverflowClient.getQuestion(questionId)
//            .map(StackOverflowQuestionResponse::lastActivityDate)
//            .doOnError(e -> log.error("StackOverflow API error for {}: {}", url, e.getMessage()));
//    }

    private boolean isGitHubUrl(String url) {
        return url.contains("github.com");
    }

    private boolean isStackOverflowUrl(String url) {
        return url.contains("stackoverflow.com");
    }
}
