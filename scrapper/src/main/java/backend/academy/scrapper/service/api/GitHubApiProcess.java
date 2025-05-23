package backend.academy.scrapper.service.api;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.dto.ApiAnswer;
import backend.academy.scrapper.client.dto.GitHubResponse;
import backend.academy.scrapper.exception.ApiClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubApiProcess implements ApiProcess {

    private final GitHubClient gitHubClient;

    @Override
    public boolean isApiUrl(String url) {
        return url.contains("github.com");
    }

    @Override
    public ApiAnswer checkUpdate(String url) {
        log.info("Processing update for github url {}", url);

        String[] parts = url.split("/");
        if (parts.length < 5) {
            log.warn("Invalid GitHub URL format: {}", url);
            return null;
        }

        String owner = parts[3];
        String repo = parts[4];
        try {
            GitHubResponse gitHubResponse =
                    gitHubClient.getRepository(owner, repo).block();
            if (gitHubResponse != null) {
                return new ApiAnswer(gitHubResponse.toMessage(), gitHubResponse.createdAt());
            }
        } catch (ApiClientException e) {
            log.error("Error parsing URL {}: ", url, e);
        }
        return null;
    }
}
