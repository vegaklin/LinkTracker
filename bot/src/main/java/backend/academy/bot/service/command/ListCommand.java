package backend.academy.bot.service.command;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

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
            if (isCorrectLinks(links)) {
                telegramMessenger.sendMessage(chatId, "Список отслеживаемых ссылок пуст");
                return;
            }

            String response = links.links()
                .stream()
                .map(this::formatLink)
                .collect(Collectors.joining("\n\n", "Отслеживаемые ссылки: \n", "\n"));
            telegramMessenger.sendMessage(chatId, response);
        } catch (ScrapperClientException e) {
            telegramMessenger.sendMessage(chatId, "Ошибка при получении списка: " + e.getMessage());
        }
    }

    private boolean isCorrectLinks(ListLinksResponse links) {
        return links == null || links.links().isEmpty();
    }

    private String formatLink(LinkResponse link) {
        String url = link.url();
        String tags = formatList(link.tags());
        String filters = formatList(link.filters());
        return String.format("""
               Ссылка: %s
               Теги: %s
               Фильтры: %s""",
            url, tags, filters
        );
    }

    private String formatList(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "отсутствуют";
        }
        return String.join(", ", items);
    }
}
