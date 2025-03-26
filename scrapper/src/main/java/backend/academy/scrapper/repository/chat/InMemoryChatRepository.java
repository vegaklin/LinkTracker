package backend.academy.scrapper.repository.chat;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class InMemoryChatRepository implements ChatRepository {

    private final Set<Long> chatIds = new HashSet<>();

    @Override
    public void registerChat(Long chatId) {
        chatIds.add(chatId);
        log.info("Chat with id {} registered", chatId);
    }

    @Override
    public void deleteChat(Long chatId) {
        chatIds.remove(chatId);
        log.info("Chat with id {} deleted", chatId);
    }

    @Override
    public Set<Long> getChatIds() {
        log.info("Get all chat IDs: {}", chatIds);
        return chatIds;
    }
}
