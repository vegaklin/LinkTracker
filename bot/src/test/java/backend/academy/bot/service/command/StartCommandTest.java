package backend.academy.bot.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.bot.client.ScrapperClient;
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
public class StartCommandTest {

    @Mock
    private TelegramMessenger telegramMessenger;

    @Mock
    private ScrapperClient scrapperClient;

    @InjectMocks
    private StartCommand startCommand;

    @Test
    void checkCommandName() {
        // given-when

        String commandName = startCommand.commandName();

        // then

        assertEquals("/start", commandName);
    }

    @Test
    void checkHandleSuccessfulRegistration() {
        // given

        Mockito.when(scrapperClient.deleteChat(1L)).thenReturn(Mono.empty());
        Mockito.when(scrapperClient.registerChat(1L)).thenReturn(Mono.empty());
        String expectedMessage =
                "Добро пожаловать! Это бот для отслеживания ссылок.\nДля получения списка доступных команд, введите /help";

        // when

        startCommand.handle(1L, "message");

        // then

        Mockito.verify(scrapperClient).deleteChat(1L);
        Mockito.verify(scrapperClient).registerChat(1L);
        Mockito.verify(telegramMessenger).sendMessage(1L, expectedMessage);
    }

    @Test
    void checkHandleScrapperClientExceptionOnDelete() {
        // given

        Mockito.when(scrapperClient.deleteChat(1L))
                .thenThrow(new ScrapperClientException(
                        new ApiErrorResponse("Delete failed", "temp", "temp", "temp", List.of())));

        // when

        startCommand.handle(1L, "message");

        // then

        Mockito.verify(scrapperClient).deleteChat(1L);
        Mockito.verify(scrapperClient, Mockito.never()).registerChat(1L);
        Mockito.verify(telegramMessenger).sendMessage(1L, "Ошибка при регистрации чата: Delete failed");
    }

    @Test
    void checkHandleScrapperClientExceptionOnRegister() {
        // given

        Mockito.when(scrapperClient.deleteChat(1L)).thenReturn(Mono.empty());
        Mockito.when(scrapperClient.registerChat(1L))
                .thenThrow(new ScrapperClientException(
                        new ApiErrorResponse("Register failed", "temp", "temp", "temp", List.of())));

        // when

        startCommand.handle(1L, "message");

        // then

        Mockito.verify(scrapperClient).deleteChat(1L);
        Mockito.verify(scrapperClient).registerChat(1L);
        Mockito.verify(telegramMessenger).sendMessage(1L, "Ошибка при регистрации чата: Register failed");
    }
}
