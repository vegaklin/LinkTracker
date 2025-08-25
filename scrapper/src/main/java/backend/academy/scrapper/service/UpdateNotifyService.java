package backend.academy.scrapper.service;

import backend.academy.scrapper.client.dto.LinkUpdate;
import backend.academy.scrapper.exception.BotClientException;
import backend.academy.scrapper.repository.ChatLinksRepository;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.service.sender.UpdateSenderService;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateNotifyService {

    private final UpdateSenderService updateSenderService;

    private final ChatLinksRepository chatLinksRepository;
    private final ChatRepository chatRepository;

    public void notifyChatsForLink(Long linkId, String url, String description) {
        log.info("Notifying chats for linkId: {}, url: {}", linkId, url);

        Set<Long> chatIds = chatRepository.getChatIds().stream()
                .filter(chatId -> chatLinksRepository.getLinksForChat(chatId).contains(linkId))
                .map(chatRepository::findChatIdById)
                .collect(Collectors.toSet());

        if (!chatIds.isEmpty()) {
            LinkUpdate update = new LinkUpdate(linkId, url, description, new ArrayList<>(chatIds));
            try {
                log.info("Sending update for linkId: {} to {} chats", linkId, chatIds.size());
                updateSenderService.sendUpdate(update);
            } catch (BotClientException e) {
                log.error("Bot client update error: ", e);
            }
        } else {
            log.info("No chats found for linkId: {}", linkId);
        }
    }
}
