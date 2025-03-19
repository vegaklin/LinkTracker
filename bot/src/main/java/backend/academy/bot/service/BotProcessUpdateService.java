package backend.academy.bot.service;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.service.model.BotState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BotProcessUpdateService {

    private final TelegramMessenger telegramMessenger;
    private final ScrapperClient scrapperClient;

    private Map<Long, String> userLinks = new HashMap<>();
    private Map<Long, String> userTags = new HashMap<>();
    private Map<Long, String> userFilters = new HashMap<>();
    private Map<Long, BotState> userStates = new HashMap<>();

    void process(long chatId, String message) {
        if (!userStates.containsKey(chatId)) {
            userStates.put(chatId, BotState.IDLE);
        }

        BotState state = userStates.get(chatId);

        if (message.equals("/start")) {
            System.out.println("add link");
            scrapperClient.registerChat(chatId).block();
            telegramMessenger.sendMessage(chatId, "Добро пожаловать! Введи /help для просмотра доступных команд.");
        } else if (message.equals("/help")) {
            telegramMessenger.sendMessage(chatId, "Команды: /start, /help, /track, /untrack, /list");
        }
        else if (message.equals("/track")) {
            userStates.put(chatId, BotState.AWAITING_LINK);
            telegramMessenger.sendMessage(chatId, "Введите ссылку для отслеживания:");
        } else if (state == BotState.AWAITING_LINK) {
            userLinks.put(chatId, message);
            userStates.put(chatId, BotState.AWAITING_TAGS);
            telegramMessenger.sendMessage(chatId, "Введите теги:");
        }
        else if (state == BotState.AWAITING_TAGS) {
            userTags.put(chatId, message);
            userStates.put(chatId, BotState.AWAITING_FILTERS);
            telegramMessenger.sendMessage(chatId, "Введите фильтры:");
        }
        else if (state == BotState.AWAITING_FILTERS) {
            userFilters.put(chatId, message);
            userStates.put(chatId, BotState.IDLE);

            String link = userLinks.get(chatId);
            String tags = userTags.getOrDefault(chatId, "Без тегов");
            String filters = userFilters.getOrDefault(chatId, "Без фильтров");

            telegramMessenger.sendMessage(chatId, "Ссылка: " + link + "\nТеги: " + tags + "\nФильтры: " + filters + "\nСсылка добавлена!");
            System.out.println("add link");
            scrapperClient.addLink(chatId, new AddLinkRequest(link, List.of(tags), List.of(filters))).block();
        }
        else if (message.startsWith("/untrack")) {
            String[] parts = message.split("\\s+");
            if (parts.length > 1) {
                scrapperClient.removeLink(chatId, new RemoveLinkRequest(parts[1])).block();
                telegramMessenger.sendMessage(chatId, "Отслеживание ссылки остановлено");
            }
        } else if (message.startsWith("/list")) {
            ListLinksResponse links = scrapperClient.getAllLinks(chatId).block();
            for (LinkResponse link : links.links()) {
                telegramMessenger.sendMessage(chatId,link.id() + " " + link.url()+" " + link.filters()+" " + link.tags());
            }
        } else {
            telegramMessenger.sendMessage(chatId, "Неизвестная команда. Введите /help для просмотра доступных команд.");
        }
    }
}
