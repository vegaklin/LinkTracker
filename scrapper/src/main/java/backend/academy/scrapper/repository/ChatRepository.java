package backend.academy.scrapper.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Repository;

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

    public Set<Long> getChatIds() {
        return Collections.unmodifiableSet(chatIds); // Return immutable view for safety
    }
}
