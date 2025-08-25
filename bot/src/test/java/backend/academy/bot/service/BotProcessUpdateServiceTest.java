package backend.academy.bot.service;

import backend.academy.bot.service.command.CommandHandler;
import backend.academy.bot.service.model.BotState;
import backend.academy.bot.service.state.TrackStateMachine;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BotProcessUpdateServiceTest {

    @Mock
    private TelegramMessenger telegramMessenger;

    @Mock
    private TrackStateMachine trackStateMachine;

    @Mock
    private CommandHandler helpCommandHandler;

    @Mock
    private CommandHandler trackCommandHandler;

    @InjectMocks
    private BotProcessUpdateService botProcessUpdateService;

    @BeforeEach
    void setUp() {
        botProcessUpdateService = new BotProcessUpdateService(
                telegramMessenger, List.of(helpCommandHandler, trackCommandHandler), trackStateMachine);
    }

    @Test
    void checkProcessNonDefaultState() {
        // given

        Mockito.when(trackStateMachine.getBotState(1L)).thenReturn(BotState.AWAITING_LINK);

        // when

        botProcessUpdateService.process(1L, "https://test.ru");

        // then

        Mockito.verify(trackStateMachine).trackProcess(1L, "https://test.ru");

        Mockito.verifyNoInteractions(helpCommandHandler, trackCommandHandler, telegramMessenger);
    }

    @Test
    void checkProcessKnownCommand() {
        // given

        Mockito.when(trackStateMachine.getBotState(1L)).thenReturn(BotState.DEFAULT);
        Mockito.when(helpCommandHandler.commandName()).thenReturn("/help");
        Mockito.when(trackCommandHandler.commandName()).thenReturn("/track");

        String message = "/track";

        // when
        botProcessUpdateService.process(1L, message);

        // then

        Mockito.verify(trackCommandHandler).handle(1L, message);
        Mockito.verify(trackStateMachine, Mockito.never()).trackProcess(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void checkProcessUnknownCommand() {
        // given

        Mockito.when(trackStateMachine.getBotState(1L)).thenReturn(BotState.DEFAULT);
        Mockito.when(helpCommandHandler.commandName()).thenReturn("/help");
        Mockito.when(trackCommandHandler.commandName()).thenReturn("/track");

        String message = "/unknown";

        // when

        botProcessUpdateService.process(1L, message);

        // then

        Mockito.verify(telegramMessenger)
                .sendMessage(1L, "Неизвестная команда. Введите /help для просмотра доступных команд");
        Mockito.verify(trackStateMachine, Mockito.never()).trackProcess(Mockito.anyLong(), Mockito.anyString());
    }
}
