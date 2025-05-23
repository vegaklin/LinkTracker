package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.model.ChatLink;
import java.util.List;

public interface ChatLinksRepository {

    List<Long> getLinksForChat(Long chatRowId);

    ChatLink getChatLinkByChatIdAndLinkId(Long chatRowId, Long linkId);

    void addLink(ChatLink chatLinks);

    boolean removeLink(Long chatRowId, Long linkId);

    void removeChatLinks(Long chatRowId);
}
