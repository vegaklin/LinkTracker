package backend.academy.bot.service;

import backend.academy.bot.dto.LinkUpdate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkUpdateSenderServiceTest {

    @Mock
    private TelegramMessenger telegramMessenger;

    @InjectMocks
    private LinkUpdateSenderService linkUpdateSenderService;

    @Test
    void checkSendLinkUpdateSingleChatId() {
        // given

        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://test.ru", "description", List.of(1L));

        // when

        linkUpdateSenderService.sendLinkUpdate(linkUpdate);

        // then

        Mockito.verify(telegramMessenger, Mockito.times(1))
                .sendMessage(1L, "Новое обновление по ссылке: https://test.ru");
    }

    @Test
    void checkSendLinkUpdateMultipleChatIds() {
        // given

        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://test.ru", "description", List.of(1L, 3L, 5L));

        // when

        linkUpdateSenderService.sendLinkUpdate(linkUpdate);

        // then

        Mockito.verify(telegramMessenger, Mockito.times(3))
                .sendMessage(Mockito.anyLong(), Mockito.eq("Новое обновление по ссылке: https://test.ru"));
        Mockito.verify(telegramMessenger, Mockito.times(1))
                .sendMessage(1L, "Новое обновление по ссылке: https://test.ru");
        Mockito.verify(telegramMessenger, Mockito.times(1))
                .sendMessage(3L, "Новое обновление по ссылке: https://test.ru");
        Mockito.verify(telegramMessenger, Mockito.times(1))
                .sendMessage(5L, "Новое обновление по ссылке: https://test.ru");
    }

    @Test
    void checkSendLinkUpdateEmptyChatIds() {
        // given

        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://test.ru", "description", List.of());

        // when

        linkUpdateSenderService.sendLinkUpdate(linkUpdate);

        // then

        Mockito.verify(telegramMessenger, Mockito.never()).sendMessage(Mockito.anyLong(), Mockito.anyString());
    }
}
