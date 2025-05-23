package backend.academy.scrapper.client;

import backend.academy.scrapper.client.dto.GitHubResponse;
import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.configuration.WebClientConfig;
import backend.academy.scrapper.exception.ApiClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GitHubClient {

    private final ScrapperConfig scrapperConfig;

    private final WebClient githubWebClient;

    public static final String REPOS_ISSUES_ENDPOINT = "/repos/{owner}/{repo}/issues";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_VALUE_PREFIX = "Bearer ";

    public GitHubClient(
            ScrapperConfig scrapperConfig, @Qualifier(WebClientConfig.GITHUB_WEB_CLIENT) WebClient githubWebClient) {
        this.scrapperConfig = scrapperConfig;
        this.githubWebClient = githubWebClient;
    }

    public Mono<GitHubResponse> getRepository(String owner, String repo) {
        return githubWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(REPOS_ISSUES_ENDPOINT).build(owner, repo))
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_VALUE_PREFIX + scrapperConfig.githubToken())
                .exchangeToMono(response -> handleResponse(response, owner, repo));
    }

    private Mono<GitHubResponse> handleResponse(ClientResponse response, String owner, String repo) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(GitHubResponse[].class)
                    .map(gitHubResponses -> {
                        if (gitHubResponses.length == 0) {
                            log.warn("No PR found for GitHub repo: {}/{}", owner, repo);
                            throw new ApiClientException("Empty Issues list");
                        }
                        return gitHubResponses[0];
                    })
                    .doOnSuccess(result -> log.info("Fetched GitHub repo: {}/{}", owner, repo));
        } else {
            return response.bodyToMono(String.class).map(error -> {
                int statusCode = response.statusCode().value();
                log.error("GitHub API error for {}/{}: status={}, body={}", owner, repo, statusCode, error);

                throw new ApiClientException(error);
            });
        }
    }
}
