package backend.academy.scrapper.repository.inmemory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import backend.academy.scrapper.repository.interfaces.ChatLinksRepository;
import backend.academy.scrapper.repository.model.ChatLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Slf4j
//@Repository
//@ConditionalOnProperty(name = "app.access-type", havingValue = "IN_MEMORY")
public class InMemoryChatLinksRepository {
    private final Map<Long, Set<ChatLink>> chatLinksMap = new HashMap<>();

//    @Override
    public List<Long> getLinksForChat(Long chatId) {
        Set<ChatLink> links = chatLinksMap.getOrDefault(chatId, Set.of());
        log.info("Get links for chatId {}: {}", chatId, links);
        return links.stream()
            .map(ChatLink::linkId)
            .toList();
    }

//    @Override
    public ChatLink getChatLinkByChatIdAndLinkId(Long chatId, Long linkId) {
        Set<ChatLink> chatLinks = chatLinksMap.get(chatId);
        if (chatLinks == null || chatLinks.isEmpty()) {
            log.warn("No links found for chatId {}", chatId);
            return null;
        }

        return chatLinks.stream()
            .filter(link -> link.chatId().equals(chatId) && link.linkId().equals(linkId))
            .findFirst()
            .orElseGet(() -> {
                log.warn("ChatLinks not found for chatId {} and linkId {}", chatId, linkId);
                return null;
            });
    }

//    @Override
    public void addLink(ChatLink chatLinks) {
        chatLinksMap.computeIfAbsent(chatLinks.chatId(), k -> new HashSet<>()).add(chatLinks);
        log.info("Link {} added to chatId {}", chatLinks.linkId(), chatLinks.chatId());
    }

//    @Override
    public boolean removeLink(Long chatId, Long linkId) {
        Set<ChatLink> chatLinks = chatLinksMap.get(chatId);

        if (chatLinks == null || chatLinks.isEmpty()) {
            log.warn("Failed to remove link {} from chatId {}: no links found", linkId, chatId);
            return false;
        }

        boolean removed = chatLinks.removeIf(link ->
            link.chatId().equals(chatId) && link.linkId().equals(linkId)
        );

        if (removed) {
            log.info("Removed link {} from chatId {}", linkId, chatId);
            if (chatLinks.isEmpty()) {
                chatLinksMap.remove(chatId);
                log.info("All links removed for chatId {}", chatId);
            }
        } else {
            log.warn("Failed to remove link {} from chatId {}: link not found", linkId, chatId);
        }

        return removed;
    }

//    @Override
    public void removeChatLinks(Long chatId) {
        chatLinksMap.remove(chatId);
        log.info("Removed all links for chatId {}", chatId);
    }
}
