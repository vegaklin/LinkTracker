package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.repository.interfaces.ChatLinksRepository;
import backend.academy.scrapper.repository.model.ChatLink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.Arrays;
import java.util.List;
import backend.academy.scrapper.service.util.ScrapperUtils;

@Slf4j
//@Repository
@RequiredArgsConstructor
//@ConditionalOnProperty(name = "app.access-type", havingValue = "SQL")
public class JdbcChatLinksRepository implements ChatLinksRepository {

    private final JdbcClient jdbc;

    @Override
    public List<Long> getLinksForChat(Long chatId) {
        return jdbc.sql("""
                SELECT link_id
                FROM chat_links
                WHERE chat_id = (SELECT id FROM chats WHERE chat_id = ?)
                """)
            .param(chatId)
            .query(Long.class)
            .list();
    }

    @Override
    public ChatLink getChatLinkByChatIdAndLinkId(Long chatId, Long linkId) {
        return jdbc.sql("""
                SELECT cl.chat_id, cl.link_id, cl.tags, cl.filters
                FROM chat_links cl
                JOIN chats c ON cl.chat_id = c.id
                WHERE c.chat_id = ? AND cl.link_id = ?
                """)
            .params(chatId, linkId)
            .query((rs, rowNum) -> new ChatLink(
                rs.getLong("chat_id"),
                rs.getLong("link_id"),
                ScrapperUtils.parseResultSetArray(rs.getArray("tags"), chatId, linkId),
                ScrapperUtils.parseResultSetArray(rs.getArray("filters"), chatId, linkId)
            ))
            .optional()
            .orElse(null);
    }

    @Override
    public void addLink(ChatLink chatLink) {
        jdbc.sql("""
                INSERT INTO chat_links (chat_id, link_id, tags, filters)
                VALUES ((SELECT id FROM chats WHERE chat_id = ?), ?, ?, ?)
                ON CONFLICT DO NOTHING
            """)
            .params(
                chatLink.chat_id(),
                chatLink.link_id(),
                chatLink.tags().toArray(new String[0]),
                chatLink.filters().toArray(new String[0])
            )
            .update();
    }

    @Override
    public boolean removeLink(Long chatId, Long linkId) {
        int updated = jdbc.sql("""
                DELETE FROM chat_links
                WHERE chat_id = (SELECT id FROM chats WHERE chat_id = ?) AND link_id = ?
                """)
            .params(chatId, linkId)
            .update();
        return updated > 0;
    }

    @Override
    public void removeChatLinks(Long chatId) {
        jdbc.sql("""
            DELETE
            FROM chat_links
            WHERE chat_id = (SELECT id FROM chats WHERE chat_id = ?)
            """)
            .param(chatId)
            .update();
    }
}
