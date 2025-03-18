package backend.academy.bot.service;

import backend.academy.bot.client.ScrapperClient;

import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.dto.LinkUpdate;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.model.BotCommand;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BotService {

    private final TelegramBot bot;
    private final ScrapperClient scrapperClient;

    private Map<Long, String> userLinks = new HashMap<>();
    private Map<Long, String> userTags = new HashMap<>();
    private Map<Long, String> userFilters = new HashMap<>();
    private Map<Long, BotState> userStates = new HashMap<>();

    @Autowired
    public BotService(TelegramBot telegramBot, ScrapperClient scrapperClient) {
        this.bot = telegramBot;
        this.scrapperClient = scrapperClient;
    }

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        bot.execute(new SetMyCommands(
            new BotCommand("/start", "регистрация пользователя"),
            new BotCommand("/help", "вывод списка доступных команд"),
            new BotCommand("/track", "начать отслеживание ссылки"),
            new BotCommand("/untrack", "прекратить отслеживание ссылки (аргумент: ссылка"),
            new BotCommand("/list", "показать список отслеживаемых ссылок")
        ));
    }

    private void processUpdate(Update update) {
        long chatId = update.message().chat().id();
        String text = update.message().text();

        if (!userStates.containsKey(chatId)) {
            userStates.put(chatId, BotState.IDLE);
        }

        BotState state = userStates.get(chatId);

        if (text.equals("/start")) {
            System.out.println("add link");
            scrapperClient.registerChat(chatId).block();
            sendMessage(chatId, "Добро пожаловать! Введи /help для просмотра доступных команд.");
        } else if (text.equals("/help")) {
            sendMessage(chatId, "Команды: /start, /help, /track, /untrack, /list");
        }
        else if (text.equals("/track")) {
            userStates.put(chatId, BotState.AWAITING_LINK);
            sendMessage(chatId, "Введите ссылку для отслеживания:");
        } else if (state == BotState.AWAITING_LINK) {
            userLinks.put(chatId, text);
            userStates.put(chatId, BotState.AWAITING_TAGS);
            sendMessage(chatId, "Введите теги:");
        }
        else if (state == BotState.AWAITING_TAGS) {
            userTags.put(chatId, text);
            userStates.put(chatId, BotState.AWAITING_FILTERS);
            sendMessage(chatId, "Введите фильтры:");
        }
        else if (state == BotState.AWAITING_FILTERS) {
            userFilters.put(chatId, text);
            userStates.put(chatId, BotState.IDLE);

            String link = userLinks.get(chatId);
            String tags = userTags.getOrDefault(chatId, "Без тегов");
            String filters = userFilters.getOrDefault(chatId, "Без фильтров");

            sendMessage(chatId, "Ссылка: " + link + "\nТеги: " + tags + "\nФильтры: " + filters + "\nСсылка добавлена!");
            System.out.println("add link");
            scrapperClient.addLink(chatId, new AddLinkRequest(link, List.of(tags), List.of(filters))).block();
        }
        else if (text.startsWith("/untrack")) {
            String[] parts = text.split("\\s+");
            if (parts.length > 1) {
                scrapperClient.removeLink(chatId, new RemoveLinkRequest(parts[1])).block();
                sendMessage(chatId, "Отслеживание ссылки остановлено");
            }
        } else if (text.startsWith("/list")) {
            ListLinksResponse links = scrapperClient.getAllLinks(chatId).block();
            for (LinkResponse link : links.links()) {
                sendMessage(chatId,link.id() + " " + link.url()+" " + link.filters()+" " + link.tags());
            }
        } else {
            sendMessage(chatId, "Неизвестная команда. Введите /help для просмотра доступных команд.");
        }
    }

    public void sendLinkUpdate(LinkUpdate linkUpdate) {
        linkUpdate.tgChatIds().forEach(chatId ->
            sendMessage(chatId, "Обновление по ссылке: " + linkUpdate.url())
        );
    }

    private void sendMessage(long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }
}
