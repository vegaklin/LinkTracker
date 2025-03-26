package backend.academy.bot.service;

import backend.academy.bot.service.command.CommandHandler;
import backend.academy.bot.service.model.BotState;
import backend.academy.bot.service.state.TrackStateMachine;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotProcessUpdateService {

    private final TelegramMessenger telegramMessenger;

    private final List<CommandHandler> commandHandlers;
    private final TrackStateMachine trackStateMachine;

    void process(Long chatId, String message) {
        log.info("Processing message '{}' from chatId {}", message, chatId);

        if (trackStateMachine.getBotState(chatId) != BotState.DEFAULT) {
            log.info(
                    "ChatId {} is in state {}, delegating to TrackStateMachine",
                    chatId,
                    trackStateMachine.getBotState(chatId));
            trackStateMachine.trackProcess(chatId, message);
            return;
        }
        for (CommandHandler commandHandler : commandHandlers) {
            if (message.startsWith(commandHandler.commandName())) {
                log.info(
                        "Command '{}' detected for chatId {}, processing with {}",
                        message,
                        chatId,
                        commandHandler.getClass().getSimpleName());
                commandHandler.handle(chatId, message);
                return;
            }
        }

        log.warn("Unknown command '{}' received from chatId {}", message, chatId);
        telegramMessenger.sendMessage(chatId, "Неизвестная команда. Введите /help для просмотра доступных команд");
    }
}
