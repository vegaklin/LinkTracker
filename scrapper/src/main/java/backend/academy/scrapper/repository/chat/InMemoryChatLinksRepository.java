package backend.academy.scrapper.repository.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class InMemoryChatLinksRepository implements ChatLinksRepository {
    private final Map<Long, Set<Long>> chatLinks = new HashMap<>();

    @Override
    public void addLink(Long chatId, Long linkId) {
        chatLinks.computeIfAbsent(chatId, k -> new HashSet<>()).add(linkId);
        log.info("Link {} added to chatId {}", linkId, chatId);
    }

    @Override
    public Set<Long> getLinksForChat(Long chatId) {
        Set<Long> links = chatLinks.getOrDefault(chatId, Set.of());
        log.info("Get links for chatId {}: {}", chatId, links);
        return links;
    }

    @Override
    public boolean removeLink(Long chatId, Long linkId) {
        Set<Long> links = chatLinks.get(chatId);
        if (links != null && links.remove(linkId)) {
            log.info("Link {} removed from chatId {}", linkId, chatId);
            return true;
        }
        log.warn("Failed to remove link {} from chatId {}", linkId, chatId);
        return false;
    }

    @Override
    public void removeChatLinks(Long chatId) {
        chatLinks.remove(chatId);
        log.info("Removed all links for chatId {}", chatId);
    }
}
