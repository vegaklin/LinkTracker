package backend.academy.scrapper.repository.chat;

import org.springframework.stereotype.Repository;
import java.util.HashSet;
import java.util.Set;

@Repository
public class InMemoryChatRepository implements ChatRepository {

    private final Set<Long> chatIds = new HashSet<>();

    @Override
    public void registerChat(Long chatId) {
        chatIds.add(chatId);
    }

    @Override
    public void deleteChat(Long chatId) {
        chatIds.remove(chatId);
    }

    @Override
    public Set<Long> getChatIds() {
        return chatIds;
    }
}
