package backend.academy.scrapper.repository.chat;

import java.util.Set;

public interface ChatLinksRepository {
    void addLink(Long chatId, Long linkId);
    Set<Long> getLinksForChat(Long chatId);
    boolean removeLink(Long chatId, Long linkId);
    void removeChatLinks(Long chatId);
}
