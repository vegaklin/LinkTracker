package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.model.Link;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
//@Repository
@RequiredArgsConstructor
//@ConditionalOnProperty(name = "app.access-type", havingValue = "SQL")
public class JdbcLinkRepository implements LinkRepository {

    private final JdbcClient jdbc;

    @Override
    public List<Link> getLinks() {
        List<Link> links = jdbc.sql("SELECT * FROM links")
            .query(Link.class)
            .list();
        log.info("Get all links. Count: {}", links.size());
        return links;
    }

    @Override
    public OffsetDateTime getUpdateTime(Long linkId) {
        return jdbc.sql("SELECT update_time FROM links WHERE id = ?")
            .param(linkId)
            .query(OffsetDateTime.class)
            .optional()
            .orElse(null);
    }

    @Override
    public String getLinkById(Long linkId) {
        return jdbc.sql("SELECT url FROM links WHERE id = ?")
            .param(linkId)
            .query(String.class)
            .optional()
            .orElse(null);
    }

    @Override
    public Long getIdByUrl(String url) {
        return jdbc.sql("SELECT id FROM links WHERE url = ?")
            .param(url)
            .query(Long.class)
            .optional()
            .orElse(null);
    }

    @Override
    public void setUpdateTime(Long linkId, OffsetDateTime updateTime) {
        jdbc.sql("UPDATE links SET update_time = ? WHERE id = ?")
            .params(updateTime, linkId)
            .update();
    }

    @Override
    public Long addLink(String url) {
        return jdbc.sql("""
            INSERT INTO links (url, description, update_time)
            VALUES (?, 'Без изменений', now())
            ON CONFLICT (url) DO UPDATE
            SET update_time = now()
            RETURNING id
        """)
            .param(url)
            .query(Long.class)
            .single();
    }
}
