package backend.academy.scrapper.repository.interfaces;

import backend.academy.scrapper.repository.model.Link;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkRepository {
    List<Link> getLinks();

    OffsetDateTime getUpdateTime(Long linkId);

    String getLinkById(Long linkId);

    Long getIdByUrl(String url);

    void setUpdateTime(Long linkId, OffsetDateTime updateTime);

    Long addLink(String link);
}
