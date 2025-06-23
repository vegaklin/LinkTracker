package backend.academy.scrapper.repository.inmemory;

import backend.academy.scrapper.repository.model.Link;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InMemoryLinkRepository {

    private final Map<Long, Link> links = new ConcurrentHashMap<>();

    private Long idCounter = 1L;

    public Map<Long, Link> getLinks() {
        log.info("Get all links. Current links count: {}", links.size());
        return links;
    }

    public OffsetDateTime getUpdateTime(Long linkId) {
        Link link = links.get(linkId);
        if (link != null) {
            log.info("Get update time for linkId: {}", linkId);
            return link.updateTime();
        }
        log.warn("Link with id {} not found while getUpdateTime", linkId);
        return null;
    }

    public void setUpdateTime(Long linkId, OffsetDateTime updateTime) {
        Link link = links.get(linkId);
        if (link != null) {
            Link updatedLink = new Link(link.id(), link.url(), link.description(), updateTime);
            links.put(linkId, updatedLink);
            log.info("Updated updateTime time for linkId: {}", linkId);
        } else {
            log.warn("Link with id {} not found to update", linkId);
        }
    }

    public Link addLink(Link link) {
        Long id = idCounter++;
        links.put(id, link);
        log.info("Added new link with id: {}", id);
        return links.get(id);
    }

    public Link getLinkById(Long linkId) {
        Link link = links.get(linkId);
        if (link != null) {
            log.info("Get link with id: {}", linkId);
            return link;
        }
        log.warn("Link with id {} not found while getLinkById", linkId);
        return null;
    }

    public Long getIdByUrl(String url) {
        Long linkId = links.entrySet().stream()
                .filter(entry -> entry.getValue().url().equals(url))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (linkId != null) {
            log.info("Found linkId: {} for URL: {}", linkId, url);
        } else {
            log.warn("No link found for URL: {}", url);
        }
        return linkId;
    }
}
