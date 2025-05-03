package backend.academy.scrapper.repository.interfaces;

import java.util.List;

public interface ChatRepository {

    void registerChat(Long chatId);
    boolean deleteChat(Long chatRowId);
    List<Long> getChatIds();
    Long findIdByChatId(Long chatId);
}
