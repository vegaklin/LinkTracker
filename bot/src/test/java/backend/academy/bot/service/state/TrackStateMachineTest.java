package backend.academy.bot.service.state;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.dto.ApiErrorResponse;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.cache.link.UserLinkRepository;
import backend.academy.bot.service.cache.state.UserStateRepository;
import backend.academy.bot.service.model.BotState;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class TrackStateMachineTest {

    @Mock
    private TelegramMessenger telegramMessenger;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private UserLinkRepository inMemoryUserLinkRepository;

    @Mock
    private UserStateRepository inMemoryUserStateRepository;

    @InjectMocks
    private TrackStateMachine trackStateMachine;

    @Test
    void checkTrackProcessSuccessful() {
        // given

        Mockito.when(inMemoryUserStateRepository.getState(1L))
                .thenReturn(BotState.AWAITING_LINK)
                .thenReturn(BotState.AWAITING_TAGS)
                .thenReturn(BotState.AWAITING_FILTERS);
        Mockito.when(scrapperClient.getAllLinks(1L)).thenReturn(Mono.just(new ListLinksResponse(List.of(), 0)));
        Mockito.when(scrapperClient.addLink(Mockito.eq(1L), Mockito.any(AddLinkRequest.class)))
                .thenReturn(
                        Mono.just(new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"))));

        // when

        trackStateMachine.trackProcess(1L, "https://test.ru");
        trackStateMachine.trackProcess(1L, "tag1 tag2");
        trackStateMachine.trackProcess(1L, "filter:filter1 filter:filter2");

        // then

        Mockito.verify(inMemoryUserLinkRepository).setLink(1L, "https://test.ru");
        Mockito.verify(inMemoryUserStateRepository).setState(1L, BotState.AWAITING_TAGS);
        Mockito.verify(telegramMessenger).sendMessage(1L, "Введите теги через пробел:");

        Mockito.verify(inMemoryUserLinkRepository).setTags(1L, List.of("tag1", "tag2"));
        Mockito.verify(inMemoryUserStateRepository).setState(1L, BotState.AWAITING_FILTERS);
        Mockito.verify(telegramMessenger).sendMessage(1L, "Введите фильтры через пробел:");

        Mockito.verify(inMemoryUserLinkRepository).setFilters(1L, List.of("filter:filter1", "filter:filter2"));
        Mockito.verify(inMemoryUserStateRepository).setState(1L, BotState.DEFAULT);
        Mockito.verify(inMemoryUserLinkRepository).clear(1L);
        Mockito.verify(telegramMessenger).sendMessage(Mockito.eq(1L), Mockito.startsWith("Добавлена ссылка:"));
    }

    @Test
    void checkHandleLinkDuplicateLink() {
        // given

        Mockito.when(inMemoryUserStateRepository.getState(1L)).thenReturn(BotState.AWAITING_LINK);
        Mockito.when(scrapperClient.getAllLinks(1L))
                .thenReturn(Mono.just(new ListLinksResponse(
                        List.of(new LinkResponse(1L, "https://test.ru", List.of(), List.of())), 1)));

        // when

        trackStateMachine.trackProcess(1L, "https://test.ru");

        // then

        Mockito.verify(telegramMessenger)
                .sendMessage(
                        1L,
                        "Ссылка уже отслеживается!\nВведите /track и повторите с новой ссылкой или /help для просмотра доступных команд");
        Mockito.verify(inMemoryUserStateRepository).setState(1L, BotState.DEFAULT);
        Mockito.verify(inMemoryUserLinkRepository, Mockito.never()).setLink(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void checkHandleLinkScrapperClientException() {
        // given

        Mockito.when(inMemoryUserStateRepository.getState(1L)).thenReturn(BotState.AWAITING_LINK);
        Mockito.when(scrapperClient.getAllLinks(1L))
                .thenThrow(new ScrapperClientException(
                        new ApiErrorResponse("Api Error", "temp", "temp", "temp", List.of())));

        // when

        trackStateMachine.trackProcess(1L, "https://test.ru");

        // then

        Mockito.verify(telegramMessenger)
                .sendMessage(1L, "Ошибка при проверке ссылки на повторение: Api Error\nПопробуйте еще раз!");
        Mockito.verify(inMemoryUserStateRepository).setState(1L, BotState.DEFAULT);
        Mockito.verify(inMemoryUserLinkRepository, Mockito.never()).setLink(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void checkTrackProcessInvalidState() {
        // given

        Mockito.when(inMemoryUserStateRepository.getState(1L)).thenReturn(BotState.DEFAULT);

        // when

        trackStateMachine.trackProcess(1L, "message");

        // then

        Mockito.verify(telegramMessenger).sendMessage(1L, "Произошла ошибко с состоянием бота, повторите команду!");
        Mockito.verifyNoInteractions(inMemoryUserLinkRepository, scrapperClient);
    }
}
