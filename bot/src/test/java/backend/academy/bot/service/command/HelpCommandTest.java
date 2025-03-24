package backend.academy.bot.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.bot.service.TelegramMessenger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {

    @Mock
    private TelegramMessenger telegramMessenger;

    @InjectMocks
    private HelpCommand helpCommand;

    @Test
    void checkCommandName() {
        // given-when

        String commandName = helpCommand.commandName();

        // then

        assertEquals("/help", commandName);
    }

    @Test
    void checkHandleSendHelpMessage() {
        // given

        String expectedMessage =
                """
            /start - регистрация пользователя
            /help - вывод списка доступных команд
            /track - начать отслеживание ссылки
            /untrack <ссылка> - прекратить отслеживание ссылки
            /list - показать список отслеживаемых ссылок""";

        // when

        helpCommand.handle(1L, "any message");

        // then

        Mockito.verify(telegramMessenger, Mockito.times(1)).sendMessage(1L, expectedMessage);
    }
}
