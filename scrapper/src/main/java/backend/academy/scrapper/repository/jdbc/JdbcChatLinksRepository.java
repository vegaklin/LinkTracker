package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.repository.ChatLinksRepository;
import backend.academy.scrapper.repository.model.ChatLink;
import backend.academy.scrapper.service.util.ScrapperUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class JdbcChatLinksRepository implements ChatLinksRepository {

    private final JdbcClient jdbc;

    @Override
    @Transactional(readOnly = true)
    public List<Long> getLinksForChat(Long chatRowId) {
        return jdbc.sql(
                        """
                SELECT link_id
                FROM chat_links
                WHERE chat_id = ?
                """)
                .param(chatRowId)
                .query(Long.class)
                .list();
    }

    @Override
    @Transactional(readOnly = true)
    public ChatLink getChatLinkByChatIdAndLinkId(Long chatRowId, Long linkId) {
        return jdbc.sql(
                        """
                SELECT chat_id, link_id, tags, filters
                FROM chat_links
                WHERE chat_id = ? AND link_id = ?
                """)
                .params(chatRowId, linkId)
                .query((rs, rowNum) -> new ChatLink(
                        rs.getLong("chat_id"),
                        rs.getLong("link_id"),
                        ScrapperUtils.parseResultSetArray(rs.getArray("tags")),
                        ScrapperUtils.parseResultSetArray(rs.getArray("filters"))))
                .optional()
                .orElse(null);
    }

    @Override
    @Transactional
    public void addLink(ChatLink chatLink) {
        jdbc.sql(
                        """
                INSERT INTO chat_links (chat_id, link_id, tags, filters)
                VALUES (?, ?, ?, ?)
                ON CONFLICT DO NOTHING
                """)
                .params(
                        chatLink.chatId(),
                        chatLink.linkId(),
                        chatLink.tags().toArray(new String[0]),
                        chatLink.filters().toArray(new String[0]))
                .update();
    }

    @Override
    @Transactional
    public boolean removeLink(Long chatRowId, Long linkId) {
        int updated = jdbc.sql(
                        """
                DELETE
                FROM chat_links
                WHERE chat_id = ? AND link_id = ?
                """)
                .params(chatRowId, linkId)
                .update();
        return updated > 0;
    }

    @Override
    @Transactional
    public void removeChatLinks(Long chatRowId) {
        jdbc.sql(
                        """
                DELETE
                FROM chat_links
                WHERE chat_id = ?
                """)
                .param(chatRowId)
                .update();
    }
}
