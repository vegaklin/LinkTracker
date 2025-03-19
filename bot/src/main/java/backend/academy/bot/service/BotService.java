package backend.academy.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.model.BotCommand;
import lombok.RequiredArgsConstructor;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotService {

    private final TelegramBot telegramBot;
    private final BotProcessUpdateService botProcessUpdateService;

    @PostConstruct
    public void init() {
        setTelegramListener();
        setTelegramMenuCommands();
    }

    private void setTelegramListener() {
        telegramBot.setUpdatesListener(updates -> {
            try {
                updates.forEach(this::processUpdate);
            } catch (RuntimeException _) {

            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void setTelegramMenuCommands(){
        try {
            telegramBot.execute(new SetMyCommands(
                new BotCommand("/start", "регистрация пользователя"),
                new BotCommand("/help", "вывод списка доступных команд"),
                new BotCommand("/track", "начать отслеживание ссылки"),
                new BotCommand("/untrack", "прекратить отслеживание ссылки (аргумент: ссылка)"),
                new BotCommand("/list", "показать список отслеживаемых ссылок")
            ));
        } catch (RuntimeException _) {

        }
    }

    private void processUpdate(Update update) {
        long chatId = update.message().chat().id();
        String message = update.message().text();
        botProcessUpdateService.process(chatId, message);
    }
}
