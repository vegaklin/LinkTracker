package backend.academy.bot.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.dto.ApiErrorResponse;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest {

    @Mock
    private TelegramMessenger telegramMessenger;

    @Mock
    private ScrapperClient scrapperClient;

    @InjectMocks
    private UntrackCommand untrackCommand;

    @Test
    void checkCommandName() {
        // given-when

        String commandName = untrackCommand.commandName();

        // then

        assertEquals("/untrack", commandName);
    }

    @Test
    void checkHandleSuccessfulUntrack() {
        // given

        String message = "/untrack https://test.ru";
        Mockito.when(scrapperClient.removeLink(Mockito.eq(1L), Mockito.any(RemoveLinkRequest.class)))
                .thenReturn(Mono.just(new LinkResponse(1L, "https://test.ru", List.of(), List.of())));

        // when

        untrackCommand.handle(1L, message);

        // then

        Mockito.verify(scrapperClient).removeLink(1L, new RemoveLinkRequest("https://test.ru"));
        Mockito.verify(telegramMessenger).sendMessage(1L, "Отслеживание ссылки успешно остановлено: https://test.ru");
    }

    @Test
    void checkHandleIncorrectCommandFormat() {
        // given

        String message = "/untrack"; // Нет ссылки

        // when

        untrackCommand.handle(1L, message);

        // then

        Mockito.verify(telegramMessenger).sendMessage(1L, "Некорректная команда! Введи с ссылкой: /untrack <ссылка>");
        Mockito.verifyNoInteractions(scrapperClient);
    }

    @Test
    void checkHandleScrapperClientException() {
        // given

        String message = "/untrack https://test.ru";
        Mockito.when(scrapperClient.removeLink(Mockito.eq(1L), Mockito.any(RemoveLinkRequest.class)))
                .thenThrow(new ScrapperClientException(
                        new ApiErrorResponse("API error", "temp", "temp", "temp", List.of())));

        // when

        untrackCommand.handle(1L, message);

        // then

        Mockito.verify(scrapperClient).removeLink(1L, new RemoveLinkRequest("https://test.ru"));
        Mockito.verify(telegramMessenger).sendMessage(1L, "Ошибка при удалении сслыки: API error");
    }
}
