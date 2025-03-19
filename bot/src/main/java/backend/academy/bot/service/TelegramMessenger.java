package backend.academy.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramMessenger {

    private final TelegramBot telegramBot;

    public void sendMessage(long chatId, String text) {
        try {
            telegramBot.execute(new SendMessage(chatId, text));
            log.info("Message successfully sent to chatId={}", chatId);
        } catch (RuntimeException e) {
            log.error("Failed to send message to chatId={}. Error: {}", chatId, e.getMessage(), e);
        }
    }
}
