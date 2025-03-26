package backend.academy.scrapper.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.client.dto.StackOverflowResponse;
import backend.academy.scrapper.exception.ApiClientException;
import java.time.Instant;
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
class StackOverflowApiProcessTest {

    @Mock
    private StackOverflowClient stackOverflowClient;

    @InjectMocks
    private StackOverflowApiProcess stackOverflowApiProcess;

    @Test
    void checkIsApiUrlValidStackOverflowUrl() {
        // given

        String url = "https://stackoverflow.com/questions/12345";

        // when-then

        assertTrue(stackOverflowApiProcess.isApiUrl(url));
    }

    @Test
    void checkIsApiUrlNonStackOverflowUrl() {
        // given

        String url = "https://notstack.com/questions/12345";

        // when-then

        assertFalse(stackOverflowApiProcess.isApiUrl(url));
    }

    @Test
    void checkCheckUpdateSuccessfulResponse() {
        // given

        String url = "https://stackoverflow.com/questions/12345/title";
        long time = 123456789L;
        OffsetDateTime lastActivityDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneOffset.UTC);
        StackOverflowResponse response = new StackOverflowResponse(time);

        Mockito.when(stackOverflowClient.getQuestion(12345L)).thenReturn(Mono.just(response));

        // when

        OffsetDateTime result = stackOverflowApiProcess.checkUpdate(url).block();

        // then

        assertNotNull(result);
        assertEquals(lastActivityDate, result);
    }

    @Test
    void checkCheckUpdateInvalidUrl() {
        // given

        String url = "https://stackoverflow.com/questions";

        // when

        OffsetDateTime result = stackOverflowApiProcess.checkUpdate(url).block();

        // then

        assertNull(result);
    }

    @Test
    void checkCheckUpdateApiClientException() {
        // given

        String url = "https://stackoverflow.com/questions/12345/title";
        Mockito.when(stackOverflowClient.getQuestion(12345L)).thenThrow(new ApiClientException("API error"));

        // when

        OffsetDateTime result = stackOverflowApiProcess.checkUpdate(url).block();

        // then

        assertNull(result);
    }
}
