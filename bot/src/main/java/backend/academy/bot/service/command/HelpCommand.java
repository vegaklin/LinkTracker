package backend.academy.bot.service.command;

import backend.academy.bot.service.TelegramMessenger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpCommand implements CommandHandler {

    private final TelegramMessenger telegramMessenger;

    @Override
    public String commandName() {
        return "/help";
    }

    @Override
    public void handle(Long chatId, String message) {
        log.info("Processing '/help' command for chatId {}", chatId);

        telegramMessenger.sendMessage(
                chatId,
                """
            /start - регистрация пользователя
            /help - вывод списка доступных команд
            /track - начать отслеживание ссылки
            /untrack <ссылка> - прекратить отслеживание ссылки
            /list - показать список отслеживаемых ссылок""");

        log.info("Help message sent to chatId {}", chatId);
    }
}
