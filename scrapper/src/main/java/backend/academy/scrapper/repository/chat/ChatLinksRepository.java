package backend.academy.scrapper.repository.chat;

import backend.academy.scrapper.exception.LinkNotFoundException;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class ChatLinksRepository {
    private final Map<Long, Set<Long>> chatLinks = new HashMap<>();

    public void addLink(Long chatId, Long linkId) {
        chatLinks.computeIfAbsent(chatId, k -> new HashSet<>()).add(linkId);
    }

    public Set<Long> getLinksForChat(Long chatId) {
        return chatLinks.getOrDefault(chatId, Set.of());
    }

    public void removeLink(Long chatId, Long linkId) {
        Set<Long> links = chatLinks.get(chatId);
        if (links != null) {
            boolean removed = links.remove(linkId);
            if (!removed) {
                throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена для чата с ID " + chatId);
            }
        } else {
            throw new LinkNotFoundException("Для чата с id " + chatId + " не найдены ссылки");
        }
    }

    public void removeChatLinks(Long chatId) {
        chatLinks.remove(chatId);
    }
}
