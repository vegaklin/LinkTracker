package backend.academy.scrapper.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.dto.GitHubResponse;
import backend.academy.scrapper.exception.ApiClientException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class GitHubApiProcessTest {

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubApiProcess gitHubApiProcess;

    @Test
    void checkIsApiUrlValidGitHubUrl() {
        // given

        String url = "https://github.com/owner/repo";

        // when-then

        assertTrue(gitHubApiProcess.isApiUrl(url));
    }

    @Test
    void checkIsApiUrlNotGitHubUrl() {
        // given

        String url = "https://nothub.com/owner/repo";

        // when-then

        assertFalse(gitHubApiProcess.isApiUrl(url));
    }

    @Test
    void checkCheckUpdateSuccessfulResponse() {
        // given

        String url = "https://github.com/owner/repo";
        OffsetDateTime updatedAt = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        GitHubResponse response = new GitHubResponse(updatedAt);

        Mockito.when(gitHubClient.getRepository("owner", "repo")).thenReturn(Mono.just(response));

        // when

        OffsetDateTime result = gitHubApiProcess.checkUpdate(url).block();

        // then

        assertNotNull(result);
        assertEquals(updatedAt, result);
    }

    @Test
    void checkCheckUpdateInvalidUrl() {
        // given

        String url = "https://github.com/owner";

        // when

        OffsetDateTime result = gitHubApiProcess.checkUpdate(url).block();

        // then

        assertNull(result);
    }

    @Test
    void checkCheckUpdateApiClientException() {
        // given

        String url = "https://github.com/owner/repo";

        Mockito.when(gitHubClient.getRepository("owner", "repo")).thenThrow(new ApiClientException("API error"));

        // when

        OffsetDateTime result = gitHubApiProcess.checkUpdate(url).block();

        // then

        assertNull(result);
    }
}
