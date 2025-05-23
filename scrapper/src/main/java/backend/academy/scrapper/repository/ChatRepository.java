package backend.academy.scrapper.repository;

import java.util.List;

public interface ChatRepository {
    void registerChat(Long chatId);

    boolean deleteChat(Long chatId);

    List<Long> getChatIds();

    Long findIdByChatId(Long chatId);

    Long findChatIdById(Long chatRowId);
}
