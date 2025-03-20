package backend.academy.bot.service.command;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.util.LinkUtils;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommand implements CommandHandler {

    private final TelegramMessenger telegramMessenger;

    private final ScrapperClient scrapperClient;

    @Override
    public String commandName() {
        return "/list";
    }

    @Override
    public void handle(Long chatId, String message) {
        try {
            ListLinksResponse links = scrapperClient.getAllLinks(chatId).block();
            if (LinkUtils.isEmptyLinks(links)) {
                telegramMessenger.sendMessage(chatId, "Список отслеживаемых ссылок пуст");
                return;
            }

            String response = links.links().stream()
                    .map(LinkUtils::formatLink)
                    .collect(Collectors.joining("\n\n", "Отслеживаемые ссылки: \n", "\n"));

            telegramMessenger.sendMessage(chatId, response);
        } catch (ScrapperClientException e) {
            telegramMessenger.sendMessage(chatId, "Ошибка при получении списка: " + e.getMessage());
        }
    }
}
