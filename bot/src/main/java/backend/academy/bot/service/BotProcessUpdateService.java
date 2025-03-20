package backend.academy.bot.service;

import backend.academy.bot.service.command.CommandHandler;
import backend.academy.bot.service.model.BotState;
import backend.academy.bot.service.state.TrackStateMachine;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotProcessUpdateService {

    private final TelegramMessenger telegramMessenger;

    private final List<CommandHandler> commandHandlers;
    private final TrackStateMachine trackStateMachine;

    void process(Long chatId, String message) {
        if (trackStateMachine.getBotState(chatId) != BotState.DEFAULT) {
            trackStateMachine.trackProcess(chatId, message);
            return;
        }
        for (CommandHandler commandHandler : commandHandlers) {
            if (message.startsWith(commandHandler.commandName())) {
                commandHandler.handle(chatId, message);
                return;
            }
        }
        telegramMessenger.sendMessage(chatId, "Неизвестная команда. Введите /help для просмотра доступных команд");
    }
}
