package backend.academy.bot.service;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.AddLinkRequest;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.client.dto.RemoveLinkRequest;
import backend.academy.bot.service.command.CommandHandler;
import backend.academy.bot.service.model.BotState;
import java.util.List;
import backend.academy.bot.service.repository.link.UserLinkRepository;
import backend.academy.bot.service.repository.state.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotProcessUpdateService {

    private final TelegramMessenger telegramMessenger;

    private final ScrapperClient scrapperClient;

    private final UserStateRepository inMemoryUserStateRepository;
    private final UserLinkRepository inMemoryUserLinkRepository;

    private final List<CommandHandler> commandHandlers;

    void process(long chatId, String message) {
        BotState state = inMemoryUserStateRepository.getState(chatId);

        if (state != BotState.DEFAULT) {
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
            return;
        }
        for (CommandHandler commandHandler : commandHandlers) {
            if (message.startsWith(commandHandler.commandName())) {
                commandHandler.handle(chatId, message);
                return;
            }
        }
        telegramMessenger.sendMessage(chatId, "Неизвестная команда. Введите /help для просмотра доступных команд");
    }
}
