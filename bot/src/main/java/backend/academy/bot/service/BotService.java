package backend.academy.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {

    private final TelegramBot telegramBot;
    private final BotProcessUpdateService botProcessUpdateService;

    @PostConstruct
    public void init() {
        log.info("Initializing Telegram bot service");
        setTelegramListener();
        setTelegramMenuCommands();
    }

    private void setTelegramListener() {
        telegramBot.setUpdatesListener(updates -> {
            log.info("Received updates from Telegram API");
            try {
                updates.forEach(this::processUpdate);
            } catch (RuntimeException e) {
                log.error("Error while processing updates", e);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void setTelegramMenuCommands() {
        try {
            log.info("Setting bot menu commands");
            telegramBot.execute(new SetMyCommands(
                    new BotCommand("/start", "регистрация пользователя"),
                    new BotCommand("/help", "вывод списка доступных команд"),
                    new BotCommand("/track", "начать отслеживание ссылки"),
                    new BotCommand("/untrack", "<ссылка> прекратить отслеживание ссылки"),
                    new BotCommand("/list", "показать список отслеживаемых ссылок")));
            log.info("Bot menu commands successfully set.");
        } catch (RuntimeException e) {
            log.error("Failed to set bot commands", e);
        }
    }

    private void processUpdate(Update update) {
        long chatId = update.message().chat().id();
        String message = update.message().text();

        log.info("Processing update: chatId={}, message={}", chatId, message);
        botProcessUpdateService.process(chatId, message);
    }
}
