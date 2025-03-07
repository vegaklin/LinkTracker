package backend.academy.bot.service;

import backend.academy.bot.client.ScrapperClient;

import backend.academy.bot.configuration.BotConfig;
import backend.academy.bot.repository.UserRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.model.BotCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class BotService {
    private static final Logger logger = LoggerFactory.getLogger(BotService.class);
    private final TelegramBot bot;
    private final UserRepository userRepository;
    private final ScrapperClient scrapperClient;

    @Autowired
    public BotService(BotConfig config, UserRepository userRepository, ScrapperClient scrapperClient) {
        this.bot = new TelegramBot(config.telegramToken());
        this.userRepository = userRepository;
        this.scrapperClient = scrapperClient;
    }

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        // Регистрация команд
        bot.execute(new SetMyCommands(
            new BotCommand("/start", "Register"),
            new BotCommand("/help", "Show commands"),
            new BotCommand("/track", "Track a link"),
            new BotCommand("/untrack", "Untrack a link"),
            new BotCommand("/list", "List tracked links")
        ));
    }

    private void processUpdate(Update update) {
        long chatId = update.message().chat().id();
        String text = update.message().text();

        logger.info("Received message", "chatId", chatId, "text", text);

        State state = userRepository.getState(chatId);

        if (text.startsWith("/start")) {
            userRepository.registerUser(chatId);
            scrapperClient.registerChat(chatId);
            sendMessage(chatId, "Welcome! Use /help for commands.");
        } else if (text.startsWith("/help")) {
            sendMessage(chatId, "Commands: /start, /help, /track, /untrack, /list");
        } else if (text.startsWith("/track")) {
            userRepository.setState(chatId, State.WAITING_FOR_LINK);
            sendMessage(chatId, "Please send the link to track");
        } else if (state == State.WAITING_FOR_LINK) {
            userRepository.setCurrentLink(chatId, text);
            userRepository.setState(chatId, State.WAITING_FOR_TAGS);
            sendMessage(chatId, "Enter tags (optional, space-separated) or 'skip'");
        } else if (state == State.WAITING_FOR_TAGS) {
            String[] tags = text.equals("skip") ? new String[0] : text.split("\\s+");
            String link = userRepository.getCurrentLink(chatId);
            scrapperClient.addLink(chatId, link, tags, new String[0]);
            userRepository.setState(chatId, State.IDLE);
            sendMessage(chatId, "Link added!");
        } else if (text.startsWith("/untrack")) {
            String[] parts = text.split("\\s+");
            if (parts.length > 1) {
                scrapperClient.removeLink(chatId, parts[1]);
                sendMessage(chatId, "Link removed!");
            }
        } else if (text.startsWith("/list")) {
            String links = scrapperClient.getLinks(chatId);
            sendMessage(chatId, links.isEmpty() ? "No tracked links" : links);
        } else {
            sendMessage(chatId, "Unknown command. Use /help for available commands.");
        }
    }

    private void sendMessage(long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }

    public void sendUpdate(long chatId, String message) {
        sendMessage(chatId, message);
    }
}
