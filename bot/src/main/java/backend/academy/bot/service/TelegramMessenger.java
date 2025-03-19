package backend.academy.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramMessenger {

    private final TelegramBot telegramBot;

    public void sendMessage(long chatId, String text) {
        try {
            telegramBot.execute(new SendMessage(chatId, text));
        } catch (RuntimeException _) {

        }
    }
}
