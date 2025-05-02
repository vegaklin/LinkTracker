package backend.academy.scrapper.repository.interfaces;

import backend.academy.scrapper.repository.model.ChatLink;
import java.util.List;

public interface ChatLinksRepository {

    List<Long> getLinksForChat(Long chatId);

    ChatLink getChatLinkByChatIdAndLinkId(Long chatId, Long linkId);

    void addLink(ChatLink chatLinks);

    boolean removeLink(Long chatId, Long linkId);

    void removeChatLinks(Long chatId);
}
