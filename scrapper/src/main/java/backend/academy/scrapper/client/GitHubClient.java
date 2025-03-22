package backend.academy.scrapper.client;

import backend.academy.scrapper.configuration.ScrapperConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubClient {

    private final ScrapperConfig scrapperConfig;
    private final WebClient githubWebClient;

    public Mono<GitHubRepoResponse> getRepository(String owner, String repo) {
        return githubWebClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .header("Authorization", "Bearer " + scrapperConfig.githubToken())
            .retrieve()
            .bodyToMono(GitHubRepoResponse.class)
            .doOnSuccess(_ -> log.info("Fetched GitHub repo: {}/{}", owner, repo))
            .doOnError(error -> log.error("Error fetching GitHub repo: {}", error.getMessage()));
    }
}
