package backend.academy.bot.service.command;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements CommandHandler {

    private final TelegramMessenger telegramMessenger;

    private final ScrapperClient scrapperClient;

    @Override
    public String commandName() {
        return "/start";
    }

    @Override
    public void handle(Long chatId, String message) {
        log.info("Processing '/start' command for chatId {}", chatId);
        try {
            scrapperClient.deleteChat(chatId).block();
            scrapperClient.registerChat(chatId).block();

            log.info("Successfully registered chat {}", chatId);
            telegramMessenger.sendMessage(
                    chatId,
                    "Добро пожаловать! Это бот для отслеживания ссылок.\nДля получения списка доступных команд, введите /help");
        } catch (ScrapperClientException e) {
            log.error("Error while registering chat {}", chatId, e);
            telegramMessenger.sendMessage(chatId, "Ошибка при регистрации чата: " + e.getMessage());
        }
    }
}
