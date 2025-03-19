package backend.academy.scrapper.client;

import backend.academy.scrapper.configuration.ScrapperConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GitHubClient {
    private final ScrapperConfig config;
    private final WebClient webClient;

    public Mono<GitHubRepoResponse> getRepository(String owner, String repo) {
        return webClient
                .get()
                .uri("https://api.github.com/repos/{owner}/{repo}", owner, repo)
                .header("Authorization", "Bearer " + config.githubToken())
                .header("Accept", "application/vnd.github.v3+json")
                .retrieve()
                .bodyToMono(GitHubRepoResponse.class);
    }
}
