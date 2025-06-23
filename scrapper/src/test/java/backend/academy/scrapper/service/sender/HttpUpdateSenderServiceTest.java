package backend.academy.scrapper.service.sender;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.dto.LinkUpdate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class HttpUpdateSenderServiceTest {

    @Mock
    private BotClient botClient;

    @InjectMocks
    private HttpUpdateSenderService updateSenderService;

    @Test
    void sendUpdate_ShouldCallBotClient() {
        // given

        LinkUpdate update = new LinkUpdate(1L, "https://test.ru", "Описание", java.util.List.of(123L));

        Mockito.when(botClient.sendUpdate(update)).thenReturn(Mono.empty());

        // when

        updateSenderService.sendUpdate(update);

        // then

        Mockito.verify(botClient, Mockito.times(1)).sendUpdate(update);
    }
}
