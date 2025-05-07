package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.model.Link;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class JdbcLinkRepository implements LinkRepository {

    private final JdbcClient jdbc;

    @Override
    @Transactional(readOnly = true)
    public List<Link> getLinks(int limit, int offset) {
        return jdbc.sql("""
                SELECT *
                FROM links
                ORDER BY id
                LIMIT ? OFFSET ?
                """)
            .params(limit, offset)
            .query(Link.class)
            .list();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countLinks() {
        return jdbc.sql("""
                SELECT COUNT(*)
                FROM links
                """)
            .query(Long.class)
            .single();
    }

    @Override
    @Transactional(readOnly = true)
    public OffsetDateTime getUpdateTime(Long linkId) {
        return jdbc.sql("""
                SELECT update_time
                FROM links
                WHERE id = ?
                """)
            .param(linkId)
            .query(OffsetDateTime.class)
            .optional()
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public String getLinkById(Long linkId) {
        return jdbc.sql("""
                SELECT url
                FROM links
                WHERE id = ?
                """)
            .param(linkId)
            .query(String.class)
            .optional()
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getIdByUrl(String url) {
        return jdbc.sql("""
                SELECT id
                FROM links
                WHERE url = ?
                """)
            .param(url)
            .query(Long.class)
            .optional()
            .orElse(null);
    }

    @Override
    @Transactional
    public void setUpdateTime(Long linkId, OffsetDateTime updateTime) {
        jdbc.sql("""
                UPDATE links
                SET update_time = ?
                WHERE id = ?
                """)
            .params(updateTime, linkId)
            .update();
    }

    @Override
    @Transactional
    public void setDescription(Long linkId, String description) {
        jdbc.sql("""
                UPDATE links
                SET description = ?
                WHERE id = ?
                """)
            .params(description, linkId)
            .update();
    }

    @Override
    @Transactional
    public Long addLink(String url) {
        jdbc.sql("""
                INSERT INTO links (url, description, update_time)
                VALUES (?, 'Без изменений', now())
                ON CONFLICT (url) DO NOTHING;
                """)
            .param(url)
            .update();
        return jdbc.sql("""
                SELECT id
                FROM links
                WHERE url = ?;
                """)
            .param(url)
            .query(Long.class)
            .single();
    }
}
