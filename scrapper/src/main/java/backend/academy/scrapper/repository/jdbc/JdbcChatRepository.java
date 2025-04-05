package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.repository.interfaces.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
//@ConditionalOnProperty(name = "app.access-type", havingValue = "SQL")
public class JdbcChatRepository implements ChatRepository {

    private final JdbcClient jdbc;

    @Override
    public void registerChat(Long chatId) {
        jdbc.sql("INSERT INTO chats (chat_id) VALUES (?) ON CONFLICT DO NOTHING")
            .params(chatId)
            .update();
        log.info("Chat with id {} registered", chatId);
    }

    @Override
    public boolean deleteChat(Long chatId) {
        int updated = jdbc.sql("DELETE FROM chats WHERE chat_id = ?")
            .params(chatId)
            .update();
        log.info("Chat with id {} deleting", chatId);
        return updated > 0;
    }

    @Override
    public List<Long> getChatIds() {
        List<Long> ids = jdbc.sql("SELECT chat_id FROM chats")
            .query(Long.class)
            .list();
        log.info("Get all chat IDs: {}", ids);
        return ids;
    }
}
