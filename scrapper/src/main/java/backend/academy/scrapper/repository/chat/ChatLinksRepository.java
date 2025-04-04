package backend.academy.scrapper.repository.chat;

import backend.academy.scrapper.repository.chat.model.ChatLink;
import java.util.List;

public interface ChatLinksRepository {

    List<Long> getLinksForChat(Long chatId);

    ChatLink getChatLinksByCharIdAndLinkId(Long chatId, Long linkId);

    void addLink(ChatLink chatLinks);

    boolean removeLink(Long chatId, Long linkId);

    void removeChatLinks(Long chatId);
}
