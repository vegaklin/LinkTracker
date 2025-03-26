package backend.academy.bot.service.command;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.util.LinkUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
        log.info("Processing '/untrack' command for chatId {}", chatId);

        String[] parts = message.trim().split(" ");
        if (LinkUtils.isCorrectParts(parts)) {
            log.warn("Invalid /untrack command format from chatId {}: {}", chatId, message);
            telegramMessenger.sendMessage(chatId, "Некорректная команда! Введи с ссылкой: /untrack <ссылка>");
            return;
        }

        String link = parts[1];
        log.info("Attempting to untrack link '{}' for chatId {}", link, chatId);
        try {
            RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(link);
            LinkResponse linkResponse =
                    scrapperClient.removeLink(chatId, removeLinkRequest).block();
            if (linkResponse != null) {
                log.info("Successfully untracked link '{}' for chatId {}", linkResponse.url(), chatId);
                telegramMessenger.sendMessage(chatId, "Отслеживание ссылки успешно остановлено: " + linkResponse.url());
            } else {
                log.warn("Received null response when untracking link '{}' for chatId {}", link, chatId);
            }
        } catch (ScrapperClientException e) {
            log.error("Error while untracking link '{}' for chatId {}: {}", link, chatId, e.getMessage(), e);
            telegramMessenger.sendMessage(chatId, "Ошибка при удалении сслыки: " + e.getMessage());
        }
    }
}
