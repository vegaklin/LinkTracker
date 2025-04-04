package backend.academy.scrapper.repository.chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    public boolean deleteChat(Long chatId) {
        log.info("Chat with id {} deleting", chatId);
        return chatIds.remove(chatId);
    }

    @Override
    public List<Long> getChatIds() {
        List<Long> chatIdList = new ArrayList<>(chatIds);
        log.info("Get all chat IDs: {}", chatIds);
        return chatIdList;
    }
}
