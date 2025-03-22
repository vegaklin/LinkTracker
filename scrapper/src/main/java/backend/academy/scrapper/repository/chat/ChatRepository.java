package backend.academy.scrapper.repository.chat;

import java.util.Set;

public interface ChatRepository {
    void registerChat(Long chatId);
    void deleteChat(Long chatId);
    Set<Long> getChatIds();
}
