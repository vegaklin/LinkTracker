package backend.academy.bot.service.command;

import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.model.BotState;
import backend.academy.bot.service.repository.state.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public void handle(long chatId, String message) {
        inMemoryUserStateRepository.setState(chatId, BotState.AWAITING_LINK);
        telegramMessenger.sendMessage(chatId, "Введите ссылку для отслеживания:");
    }
}
