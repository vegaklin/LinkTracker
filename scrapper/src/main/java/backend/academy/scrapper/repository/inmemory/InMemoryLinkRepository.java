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
