package backend.academy.scrapper.repository.link;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.repository.link.model.Link;
import java.time.OffsetDateTime;
import java.util.Map;

public interface LinkRepository {
    Map<Long, Link> getLinks();
    OffsetDateTime getUpdateTime(Long linkId);
    void setUpdateTime(Long linkId, OffsetDateTime updateTime);
    LinkResponse addLink(Link link);
    LinkResponse getLinkById(Long linkId);
    Long getIdByUrl(String url);
}
