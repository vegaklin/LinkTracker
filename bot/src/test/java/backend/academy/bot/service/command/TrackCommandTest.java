package backend.academy.bot.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.cache.state.UserStateRepository;
import backend.academy.bot.service.model.BotState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest {

    @Mock
    private TelegramMessenger telegramMessenger;

    @Mock
    private UserStateRepository inMemoryUserStateRepository;

    @InjectMocks
    private TrackCommand trackCommand;

    @Test
    void checkCommandName() {
        // given-when

        String commandName = trackCommand.commandName();

        // then

        assertEquals("/track", commandName);
    }

    @Test
    void checkHandleSetStateAndSendMessage() {
        // given

        String expectedMessage = "Введите ссылку для отслеживания:";

        // when

        trackCommand.handle(1L, "message");

        // then

        Mockito.verify(inMemoryUserStateRepository).setState(1L, BotState.AWAITING_LINK);
        Mockito.verify(telegramMessenger).sendMessage(1L, expectedMessage);
    }
}
