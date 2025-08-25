package backend.academy.bot.service.state;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.cache.link.UserLinkRepository;
import backend.academy.bot.service.cache.state.UserStateRepository;
import backend.academy.bot.service.model.BotState;
import backend.academy.bot.service.util.LinkUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
        log.info("Processing message '{}' for chatId {}", message, chatId);
        switch (getBotState(chatId)) {
            case AWAITING_LINK -> handleLink(chatId, message);
            case AWAITING_TAGS -> handleTags(chatId, message);
            case AWAITING_FILTERS -> handleFilters(chatId, message);
            default -> {
                log.error("Unexpected bot state for chatId {}: {}", chatId, getBotState(chatId));
                telegramMessenger.sendMessage(chatId, "Произошла ошибка с состоянием бота, повторите команду!");
            }
        }
    }

    private void handleLink(Long chatId, String message) {
        log.info("Handling link for chatId {}: {}", chatId, message);
        try {
            ListLinksResponse links = scrapperClient.getAllLinks(chatId).block();
            if (LinkUtils.isAnyMatchLinks(links, message)) {
                telegramMessenger.sendMessage(
                        chatId,
                        "Ссылка уже отслеживается!\nВведите /track и повторите с новой ссылкой или /help для просмотра доступных команд");
                inMemoryUserStateRepository.setState(chatId, BotState.DEFAULT);
                log.info("Link already tracked for chatId {}", chatId);
                return;
            }
        } catch (ScrapperClientException e) {
            log.error("Error checking link for chatId {}", chatId, e);
            telegramMessenger.sendMessage(
                    chatId, "Ошибка при проверке ссылки на повторение: " + e.getMessage() + "\nПопробуйте еще раз!");
            inMemoryUserStateRepository.setState(chatId, BotState.DEFAULT);
            return;
        }

        inMemoryUserLinkRepository.setLink(chatId, message);
        inMemoryUserStateRepository.setState(chatId, BotState.AWAITING_TAGS);
        telegramMessenger.sendMessage(chatId, "Введите теги через пробел:");
        log.info("Link set for chatId {}, moving to AWAITING_TAGS state", chatId);
    }

    private void handleTags(Long chatId, String message) {
        log.info("Handling tags for chatId {}: {}", chatId, message);
        inMemoryUserLinkRepository.setTags(chatId, LinkUtils.splitFiltersAndTags(message));
        inMemoryUserStateRepository.setState(chatId, BotState.AWAITING_FILTERS);
        telegramMessenger.sendMessage(chatId, "Введите фильтры через пробел:");
        log.info("Tags set for chatId {}, moving to AWAITING_FILTERS state", chatId);
    }

    private void handleFilters(Long chatId, String message) {
        log.info("Handling filters for chatId {}: {}", chatId, message);
        inMemoryUserLinkRepository.setFilters(chatId, LinkUtils.splitFiltersAndTags(message));
        inMemoryUserStateRepository.setState(chatId, BotState.DEFAULT);

        String link = inMemoryUserLinkRepository.getLink(chatId);
        List<String> tags = inMemoryUserLinkRepository.getTags(chatId);
        List<String> filters = inMemoryUserLinkRepository.getFilters(chatId);

        inMemoryUserLinkRepository.clear(chatId);

        try {
            LinkResponse linkResponse = scrapperClient
                    .addLink(chatId, new AddLinkRequest(link, tags, filters))
                    .block();
            if (linkResponse != null) {
                telegramMessenger.sendMessage(chatId, "Добавлена ссылка:\n" + LinkUtils.formatLink(linkResponse));
                log.info("Link successfully added for chatId {}: {}", chatId, LinkUtils.formatLink(linkResponse));
            } else {
                log.warn("Received null response when adding link '{}' for chatId {}", link, chatId);
            }
        } catch (ScrapperClientException e) {
            log.error("Error saving link for chatId {}", chatId, e);
            telegramMessenger.sendMessage(chatId, "Ошибка при сохранении ссылки: " + e.getMessage());
        }
    }
}
