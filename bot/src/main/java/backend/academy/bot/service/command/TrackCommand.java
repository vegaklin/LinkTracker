package backend.academy.bot.service.command;

import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.cache.state.UserStateRepository;
import backend.academy.bot.service.model.BotState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackCommand implements CommandHandler {

    private final TelegramMessenger telegramMessenger;

    private final UserStateRepository inMemoryUserStateRepository;

    @Override
    public String commandName() {
        return "/track";
    }

    @Override
    public void handle(Long chatId, String message) {
        log.info("Processing '/track' command for chatId {}", chatId);

        inMemoryUserStateRepository.setState(chatId, BotState.AWAITING_LINK);
        log.info("Set bot state to AWAITING_LINK for chatId {}", chatId);

        telegramMessenger.sendMessage(chatId, "Введите ссылку для отслеживания:");
    }
}
