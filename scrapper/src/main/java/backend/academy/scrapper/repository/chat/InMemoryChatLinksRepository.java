package backend.academy.scrapper.repository.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryChatLinksRepository implements ChatLinksRepository {
    private final Map<Long, Set<Long>> chatLinks = new HashMap<>();

    @Override
    public void addLink(Long chatId, Long linkId) {
        chatLinks.computeIfAbsent(chatId, k -> new HashSet<>()).add(linkId);
    }

    @Override
    public Set<Long> getLinksForChat(Long chatId) {
        return chatLinks.getOrDefault(chatId, Set.of());
    }

    @Override
    public boolean removeLink(Long chatId, Long linkId) {
        Set<Long> links = chatLinks.get(chatId);
        if (links != null) {
            return links.remove(linkId);
        }
        return false;
    }

    @Override
    public void removeChatLinks(Long chatId) {
        chatLinks.remove(chatId);
    }
}
