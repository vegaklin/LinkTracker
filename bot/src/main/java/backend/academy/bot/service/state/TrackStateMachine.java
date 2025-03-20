package backend.academy.bot.service.state;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.model.BotState;
import backend.academy.bot.service.repository.link.UserLinkRepository;
import backend.academy.bot.service.repository.state.UserStateRepository;
import backend.academy.bot.service.util.LinkUtils;
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

    public void trackProcess(Long chatId, String message) {
        switch (getBotState(chatId)) {
            case AWAITING_LINK -> handleLink(chatId, message);
            case AWAITING_TAGS -> handleTags(chatId, message);
            case AWAITING_FILTERS -> handleFilters(chatId, message);
        }
    }

    private void handleLink(Long chatId, String message) {
        try {
            ListLinksResponse links = scrapperClient.getAllLinks(chatId).block();
            if (LinkUtils.isAnyMatchLinks(links, message)) {
                telegramMessenger.sendMessage(
                    chatId,
                    "Эта ссылка уже добавлена! Введите /track и повторите с новой ссылкой или /help"
                );
                inMemoryUserStateRepository.setState(chatId, BotState.DEFAULT);
                return;
            }
        } catch (ScrapperClientException e) {
            telegramMessenger.sendMessage(chatId,
                "Ошибка при проверке ссылкина повторение: "
                + e.getMessage()
                + "\nПопробуйте еще раз!"
            );
            inMemoryUserStateRepository.setState(chatId, BotState.DEFAULT);
        }

        inMemoryUserLinkRepository.setLink(chatId, message);
        inMemoryUserStateRepository.setState(chatId, BotState.AWAITING_TAGS);
        telegramMessenger.sendMessage(chatId, "Введите теги через пробел:");
    }

    private void handleTags(Long chatId, String message) {
        inMemoryUserLinkRepository.setTags(chatId, LinkUtils.splitFiltersAndTags(message));
        inMemoryUserStateRepository.setState(chatId, BotState.AWAITING_FILTERS);
        telegramMessenger.sendMessage(chatId, "Введите фильтры через пробел:");
    }

    private void handleFilters(Long chatId, String message) {
        inMemoryUserLinkRepository.setFilters(chatId, LinkUtils.splitFiltersAndTags(message));

        String link = inMemoryUserLinkRepository.getLink(chatId);
        List<String> tags = inMemoryUserLinkRepository.getTags(chatId);
        List<String> filters = inMemoryUserLinkRepository.getFilters(chatId);

        inMemoryUserLinkRepository.clear(chatId);

        try {
            LinkResponse linkResponse = scrapperClient.addLink(chatId, new AddLinkRequest(link, tags, filters)).block();
            if (linkResponse != null) {
                telegramMessenger.sendMessage(chatId, "Добавлена ссылка:\n" + LinkUtils.formatLink(linkResponse));
            }
        } catch (ScrapperClientException e) {
            telegramMessenger.sendMessage(chatId, "Ошибка при созранении ссылки: " + e.getMessage());
        }
    }
}
