package backend.academy.scrapper.repository.link;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.repository.link.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public interface LinkRepository {
    List<Link> getLinks();

    OffsetDateTime getUpdateTime(Long linkId);

    String getLinkById(Long linkId);

    Long getIdByUrl(String url);

    void setUpdateTime(Long linkId, OffsetDateTime updateTime);

    Long addLink(String link);
}
