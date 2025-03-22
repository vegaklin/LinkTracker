package backend.academy.scrapper.service.api;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.dto.GitHubResponse;
import backend.academy.scrapper.exception.ApiClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubApiProcess implements ApiProcess{

    private final GitHubClient gitHubClient;

    public boolean isApiUrl(String url) {
        return url.contains("github.com");
    }

    public Mono<OffsetDateTime> checkUpdate(String url) {
        String[] parts = url.split("/");
        if (parts.length < 5) {
            log.warn("Invalid GitHub URL format: {}", url);
            return Mono.empty();
        }

        try {
            String owner = parts[3];
            String repo = parts[4];
            return gitHubClient.getRepository(owner, repo)
                .map(GitHubResponse::updatedAt);
        } catch (ApiClientException e) {
            log.error("Error parsing URL {}: ", url, e);
        }
        return Mono.empty();
    }
}
