package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.model.Link;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkRepository {
    List<Link> getLinks(int limit, int offset);

    Long countLinks();

    OffsetDateTime getUpdateTime(Long linkId);

    String getLinkById(Long linkId);

    Long getIdByUrl(String url);

    void setUpdateTime(Long linkId, OffsetDateTime updateTime);

    void setDescription(Long linkId, String description);

    Long addLink(String link);
}
