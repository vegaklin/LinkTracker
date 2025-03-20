package backend.academy.bot.service.command;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public void handle(long chatId, String message) {
        try {
            scrapperClient.deleteChat(chatId).block();
            scrapperClient.registerChat(chatId).block();
            telegramMessenger.sendMessage(chatId, "Добро пожаловать! Это бот для отслеживания ссылок. Для получения списка доступных команд, введите /help");
        } catch (ScrapperClientException e) {
            telegramMessenger.sendMessage(chatId,"Ошибка при регистрации чата: " + e.getMessage());
        }
    }
}
