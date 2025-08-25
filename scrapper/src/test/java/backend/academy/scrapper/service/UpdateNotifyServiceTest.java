package backend.academy.scrapper.service;

import backend.academy.scrapper.client.dto.LinkUpdate;
import backend.academy.scrapper.dto.ApiErrorResponse;
import backend.academy.scrapper.exception.BotClientException;
import backend.academy.scrapper.repository.ChatLinksRepository;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.service.sender.UpdateSenderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateNotifyServiceTest {

    @Mock
    private UpdateSenderService updateSenderService;

    @Mock
    private ChatLinksRepository chatLinksRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private UpdateNotifyService updateNotifyService;

    @Test
    void checkNotifyChatsForLinkSuccessfulSend() {
        // given

        List<Long> allChatIds = List.of(1L, 2L, 3L);

        Mockito.when(chatRepository.getChatIds()).thenReturn(allChatIds);
        Mockito.when(chatLinksRepository.getLinksForChat(1L)).thenReturn(List.of(1L));
        Mockito.when(chatLinksRepository.getLinksForChat(2L)).thenReturn(List.of(1L));
        Mockito.when(chatLinksRepository.getLinksForChat(3L)).thenReturn(List.of(3L));
        Mockito.when(chatRepository.findChatIdById(Mockito.anyLong()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when

        updateNotifyService.notifyChatsForLink(1L, "https://test.ru", "Обнаружено обновление");

        // then

        Mockito.verify(chatRepository).getChatIds();
        Mockito.verify(chatLinksRepository).getLinksForChat(1L);
        Mockito.verify(chatLinksRepository).getLinksForChat(2L);
        Mockito.verify(chatLinksRepository).getLinksForChat(3L);
        Mockito.verify(updateSenderService)
                .sendUpdate(Mockito.argThat(update -> update.id().equals(1L)
                        && update.url().equals("https://test.ru")
                        && update.description().equals("Обнаружено обновление")
                        && update.tgChatIds().equals(List.of(1L, 2L))));
    }

    @Test
    void checkNotifyChatsForLinkNoChatsFound() {
        // given

        List<Long> allChatIds = List.of(1L, 2L);
        Mockito.when(chatRepository.getChatIds()).thenReturn(allChatIds);
        Mockito.when(chatLinksRepository.getLinksForChat(1L)).thenReturn(List.of(2L));
        Mockito.when(chatLinksRepository.getLinksForChat(2L)).thenReturn(List.of(3L));

        // when

        updateNotifyService.notifyChatsForLink(1L, "https://test.ru", "Обнаружено обновление");

        // then

        Mockito.verify(updateSenderService, Mockito.never()).sendUpdate(Mockito.any());
    }

    @Test
    void checkNotifyChatsForLinkBotClientException() {
        // given

        List<Long> allChatIds = List.of(1L);
        Mockito.when(chatRepository.getChatIds()).thenReturn(allChatIds);
        Mockito.when(chatLinksRepository.getLinksForChat(1L)).thenReturn(List.of(1L));
        Mockito.when(chatRepository.findChatIdById(1L)).thenReturn(1L);
        Mockito.doThrow(new BotClientException(new ApiErrorResponse("Api Error", "temp", "temp", "temp", List.of())))
                .when(updateSenderService)
                .sendUpdate(Mockito.any(LinkUpdate.class));

        // when

        updateNotifyService.notifyChatsForLink(1L, "https://test.ru", "Обнаружено обновление");

        // then

        Mockito.verify(updateSenderService)
                .sendUpdate(Mockito.argThat(update -> update.id().equals(1L)
                        && update.url().equals("https://test.ru")
                        && update.tgChatIds().equals(List.of(1L))));
    }
}
