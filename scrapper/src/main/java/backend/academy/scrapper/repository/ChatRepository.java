package backend.academy.scrapper.repository;

import org.springframework.stereotype.Repository;
import java.util.HashSet;
import java.util.Set;

@Repository
public class ChatRepository {
    private final Set<Long> chatIds = new HashSet<>();

    public void registerChat(Long chatId) {
        chatIds.add(chatId);
    }

    public void deleteChat(Long chatId) {
        chatIds.remove(chatId);
    }

    public boolean existsById(Long chatId) {
        return chatIds.contains(chatId);
    }
}
