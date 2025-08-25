package backend.academy.scrapper.repository.inmemory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InMemoryChatRepository {

    private final Set<Long> chatIds = new CopyOnWriteArraySet<>();

    public void registerChat(Long chatId) {
        chatIds.add(chatId);
        log.info("Chat with id {} registered", chatId);
    }

    public boolean deleteChat(Long chatId) {
        log.info("Chat with id {} deleting", chatId);
        return chatIds.remove(chatId);
    }

    public Set<Long> getChatIds() {
        log.info("Get all chat IDs: {}", chatIds);
        return chatIds;
    }
}
