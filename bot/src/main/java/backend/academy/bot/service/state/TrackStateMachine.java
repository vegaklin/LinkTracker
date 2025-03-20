package backend.academy.bot.service.state;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.model.BotState;
import backend.academy.bot.service.repository.link.UserLinkRepository;
import backend.academy.bot.service.repository.state.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrackStateMachine {

    private final TelegramMessenger telegramMessenger;

    private final ScrapperClient scrapperClient;

    private final UserLinkRepository inMemoryUserLinkRepository;
    private final UserStateRepository inMemoryUserStateRepository;

    public BotState getBotState(Long chatId) {
        return inMemoryUserStateRepository.getState(chatId);
    }

    public void trackProcess(
        BotState state,
        Long chatId,
        String message
    ) {
        if (state == BotState.AWAITING_LINK) {
            inMemoryUserLinkRepository.setLink(chatId, message);
            inMemoryUserStateRepository.setState(chatId, BotState.AWAITING_TAGS);
            telegramMessenger.sendMessage(chatId, "Введите теги:");
        } else if (state == BotState.AWAITING_TAGS) {
            inMemoryUserLinkRepository.setTags(chatId, List.of(message));
            inMemoryUserStateRepository.setState(chatId, BotState.AWAITING_FILTERS);
            telegramMessenger.sendMessage(chatId, "Введите фильтры:");
        } else if (state == BotState.AWAITING_FILTERS) {
            inMemoryUserLinkRepository.setFilters(chatId, List.of(message));
            inMemoryUserStateRepository.setState(chatId, BotState.DEFAULT);

            String link = inMemoryUserLinkRepository.getLink(chatId);
            String tags = inMemoryUserLinkRepository.getTags(chatId).getFirst();
            String filters = inMemoryUserLinkRepository.getFilters(chatId).getFirst();

            telegramMessenger.sendMessage(
                chatId, "Ссылка: " + link + "\nТеги: " + tags + "\nФильтры: " + filters + "\nСсылка добавлена!");
            System.out.println("add link");
            scrapperClient
                .addLink(chatId, new AddLinkRequest(link, List.of(tags), List.of(filters)))
                .block();
        }
    }
}
