package backend.academy.scrapper.repository.link;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.repository.link.model.Link;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLinkRepository implements LinkRepository {

    private final Map<Long, Link> links = new HashMap<>();

    private Long idCounter = 1L;

    @Override
    public Map<Long, Link> getLinks() {
        return links;
    }

    @Override
    public OffsetDateTime getUpdateTime(Long linkId) {
        return links.get(linkId).updateTime();
    }

    @Override
    public void setUpdateTime(Long linkId, OffsetDateTime updateTime) {
        Link link = links.get(linkId);
        Link updatedLink = new Link(
            link.url(),
            link.tags(),
            link.filters(),
            updateTime
        );
        links.put(linkId, updatedLink);
    }

    @Override
    public LinkResponse addLink(Link link) {
        Long id = idCounter++;
        links.put(id, link);
        return new LinkResponse(id, link.url(), link.tags(), link.filters());
    }

    @Override
    public LinkResponse getLinkById(Long linkId) {
        Link link = links.get(linkId);
        if (link != null) {
            return new LinkResponse(linkId, link.url(), link.tags(), link.filters());
        }
        return null;
    }

    @Override
    public Long getIdByUrl(String url) {
        return links.entrySet().stream()
            .filter(entry -> entry.getValue().url().equals(url))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }
}
