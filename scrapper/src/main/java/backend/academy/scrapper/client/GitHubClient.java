package backend.academy.scrapper.client;

import backend.academy.scrapper.client.dto.GitHubResponse;
import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.exception.ApiClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubClient {

    private final ScrapperConfig scrapperConfig;

    private final WebClient githubWebClient;

    public Mono<GitHubResponse> getRepository(String owner, String repo) {
        return githubWebClient
                .get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .header("Authorization", "Bearer " + scrapperConfig.githubToken())
                .exchangeToMono(response -> handleResponse(response, GitHubResponse.class, owner, repo));
    }

    private <T> Mono<T> handleResponse(ClientResponse response, Class<T> responseType, String owner, String repo) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(responseType)
                    .doOnSuccess(result -> log.info("Fetched GitHub repo: {}/{}: {}", owner, repo, result));
        } else {
            return response.bodyToMono(String.class).map(error -> {
                int statusCode = response.statusCode().value();
                log.error("GitHub API error for {}/{}: status={}, body={}", owner, repo, statusCode, error);
                throw new ApiClientException(error);
            });
        }
    }
}
