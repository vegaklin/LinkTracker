package backend.academy.scrapper.service;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.dto.LinkUpdate;
import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.exception.BotClientException;
import backend.academy.scrapper.repository.chat.ChatLinksRepository;
import backend.academy.scrapper.repository.chat.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UpdateSenderServiceTest {

    @Mock
    private BotClient botClient;

    @Mock
    private ChatLinksRepository chatLinksRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private UpdateSenderService updateSenderService;

    @Test
    void checkNotifyChatsForLinkSuccessfulSend() {
        // given

        Set<Long> allChatIds = Set.of(1L, 2L, 3L);

        Mockito.when(chatRepository.getChatIds()).thenReturn(allChatIds);
        Mockito.when(chatLinksRepository.getLinksForChat(1L)).thenReturn(Set.of(1L));
        Mockito.when(chatLinksRepository.getLinksForChat(2L)).thenReturn(Set.of(1L));
        Mockito.when(chatLinksRepository.getLinksForChat(3L)).thenReturn(Set.of(3L));

        Mockito.when(botClient.sendUpdate(Mockito.any(LinkUpdate.class))).thenReturn(Mono.empty());

        // when

        updateSenderService.notifyChatsForLink(1L, "https://test.ru");

        // then

        Mockito.verify(chatRepository).getChatIds();
        Mockito.verify(chatLinksRepository).getLinksForChat(1L);
        Mockito.verify(chatLinksRepository).getLinksForChat(2L);
        Mockito.verify(botClient).sendUpdate(Mockito.argThat(update ->
            update.id().equals(1L) &&
                update.url().equals("https://test.ru") &&
                update.description().equals("Обнаружено обновление") &&
                update.tgChatIds().equals(List.of(1L, 2L))
        ));
    }

    @Test
    void checkNotifyChatsForLinkNoChatsFound() {
        // given

        Set<Long> allChatIds = Set.of(1L, 2L);
        Mockito.when(chatRepository.getChatIds()).thenReturn(allChatIds);
        Mockito.when(chatLinksRepository.getLinksForChat(1L)).thenReturn(Set.of(2L));
        Mockito.when(chatLinksRepository.getLinksForChat(2L)).thenReturn(Set.of(3L));

        // when

        updateSenderService.notifyChatsForLink(1L, "https://test.ru");

        // then

        Mockito.verify(chatRepository).getChatIds();
        Mockito.verify(chatLinksRepository).getLinksForChat(1L);
        Mockito.verify(chatLinksRepository).getLinksForChat(2L);
        Mockito.verify(botClient, Mockito.never()).sendUpdate(Mockito.any());
    }

    @Test
    void checkNotifyChatsForLinkBotClientException() {
        // given

        Set<Long> allChatIds = Set.of(1L);

        Mockito.when(chatRepository.getChatIds()).thenReturn(allChatIds);
        Mockito.when(chatLinksRepository.getLinksForChat(1L)).thenReturn(Set.of(1L));
        Mockito.when(botClient.sendUpdate(Mockito.any(LinkUpdate.class))).thenThrow(new BotClientException(new ApiErrorResponse("Api Error", "temp", "temp", "temp", List.of())));

        // when

        updateSenderService.notifyChatsForLink(1L, "https://test.ru");

        // then

        Mockito.verify(chatRepository).getChatIds();
        Mockito.verify(chatLinksRepository).getLinksForChat(1L);
        Mockito.verify(botClient).sendUpdate(Mockito.argThat(update ->
            update.id().equals(1L) &&
                update.url().equals("https://test.ru") &&
                update.tgChatIds().equals(List.of(1L))
        ));
    }
}
