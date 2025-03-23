package backend.academy.scrapper.service;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.dto.LinkUpdate;
import backend.academy.scrapper.exception.BotClientException;
import backend.academy.scrapper.repository.chat.ChatLinksRepository;
import backend.academy.scrapper.repository.chat.ChatRepository;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateSenderService {

    private final BotClient botClient;

    private final ChatLinksRepository chatLinksRepository;
    private final ChatRepository chatRepository;

    public void notifyChatsForLink(Long linkId, String url) {
        Set<Long> chatIds = chatRepository.getChatIds().stream()
                .filter(chatId -> chatLinksRepository.getLinksForChat(chatId).contains(linkId))
                .collect(Collectors.toSet());

        if (!chatIds.isEmpty()) {
            LinkUpdate update = new LinkUpdate(linkId, url, "Обнаружено обновление", new ArrayList<>(chatIds));
            try {
                botClient.sendUpdate(update).block();
            } catch (BotClientException e) {
                log.error("Bot client update error: ", e);
            }
        } else {
            log.info("No chats found for linkId: {}", linkId);
        }
    }
}
