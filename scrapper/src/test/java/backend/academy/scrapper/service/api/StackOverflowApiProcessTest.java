package backend.academy.scrapper.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.client.dto.ApiAnswer;
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

        String url = "https://stackoverflow.com/questions/12345/example-question";
        long questionId = 12345L;
        long creationTime = 123456789L;
        OffsetDateTime expectedTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(creationTime), ZoneOffset.UTC);

        StackOverflowResponse response = new StackOverflowResponse(
                "Question title",
                new StackOverflowResponse.StackOverflowOwner("StackUser"),
                creationTime,
                "Question body");

        Mockito.when(stackOverflowClient.getQuestion(questionId)).thenReturn(Mono.just(response));

        // when

        ApiAnswer answer = stackOverflowApiProcess.checkUpdate(url);

        // then

        assertNotNull(answer);
        assertTrue(answer.description().contains("Название Answer"));
        assertEquals(expectedTime, answer.lastUpdate());
        Mockito.verify(stackOverflowClient, Mockito.times(1)).getQuestion(questionId);
    }

    @Test
    void checkCheckUpdateInvalidUrl() {
        // given

        String url = "https://stackoverflow.com/questions";

        // when

        ApiAnswer answer = stackOverflowApiProcess.checkUpdate(url);

        // then

        assertNull(answer);
        Mockito.verifyNoInteractions(stackOverflowClient);
    }

    @Test
    void checkCheckUpdateInvalidId() {
        // given

        String url = "https://stackoverflow.com/questions/abcde/invalid";

        // when

        ApiAnswer answer = stackOverflowApiProcess.checkUpdate(url);

        // then

        assertNull(answer);
        Mockito.verifyNoInteractions(stackOverflowClient);
    }

    @Test
    void checkClientReturnsNullReturnsNull() {
        // given

        String url = "https://stackoverflow.com/questions/12345/example";
        Mockito.when(stackOverflowClient.getQuestion(12345L)).thenReturn(Mono.empty());

        // when

        ApiAnswer answer = stackOverflowApiProcess.checkUpdate(url);

        // then

        assertNull(answer);
    }

    @Test
    void checkCheckUpdateApiClientException() {
        // given

        String url = "https://stackoverflow.com/questions/12345/example";
        Mockito.when(stackOverflowClient.getQuestion(12345L)).thenThrow(new ApiClientException("Failed to fetch"));

        // when

        ApiAnswer answer = stackOverflowApiProcess.checkUpdate(url);

        // then

        assertNull(answer);
    }
}
