package backend.academy.bot.service.command;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.util.LinkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UntrackCommand implements CommandHandler {

    private final TelegramMessenger telegramMessenger;

    private final ScrapperClient scrapperClient;

    @Override
    public String commandName() {
        return "/untrack";
    }

    @Override
    public void handle(Long chatId, String message) {
        String[] parts = message.trim().split(" ");
        if (LinkUtils.isCorrectParts(parts)) {
            telegramMessenger.sendMessage(chatId, "Некорректная команда! Введи с ссылкой: /untrack <ссылка>");
            return;
        }

        try {
            RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(parts[1]);
            LinkResponse linkResponse =
                    scrapperClient.removeLink(chatId, removeLinkRequest).block();
            if (linkResponse != null) {
                telegramMessenger.sendMessage(chatId, "Отслеживание ссылки успешно остановлено: " + linkResponse.url());
            }
        } catch (ScrapperClientException e) {
            telegramMessenger.sendMessage(chatId, "Ошибка при удалении сслыки: " + e.getMessage());
        }
    }
}
