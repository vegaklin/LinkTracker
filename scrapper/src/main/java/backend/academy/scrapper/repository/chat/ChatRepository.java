package backend.academy.scrapper.repository.chat;

import java.util.List;

public interface ChatRepository {
    void registerChat(Long chatId);

    boolean deleteChat(Long chatId);

    List<Long> getChatIds();
}
