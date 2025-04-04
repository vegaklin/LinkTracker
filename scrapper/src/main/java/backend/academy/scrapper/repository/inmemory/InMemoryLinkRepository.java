package backend.academy.scrapper.repository.inmemory;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.model.Link;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class InMemoryLinkRepository implements LinkRepository {

    private final Map<Long, Link> links = new HashMap<>();

    private Long idCounter = 1L;

    @Override
    public List<Link> getLinks() {
        List<Link> linkList = new ArrayList<>(links.values());
        log.info("Get all links. Current links count: {}", links.size());
        return linkList;
    }

    @Override
    public OffsetDateTime getUpdateTime(Long linkId) {
        Link link = links.get(linkId);
        if (link != null) {
            log.info("Get update time for linkId: {}", linkId);
            return link.updateTime();
        }
        log.warn("Link with id {} not found while getUpdateTime", linkId);
        return null;
    }

    @Override
    public String getLinkById(Long linkId) {
        Link link = links.get(linkId);
        if (link != null) {
            log.info("Get link with id: {}", linkId);
            return link.url();
        }
        log.warn("Link with id {} not found while getLinkById", linkId);
        return null;
    }

    @Override
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

    @Override
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

    @Override
    public Long addLink(String link) {
        Long id = idCounter++;
        links.put(id, new Link(id, link, "Без изменений", OffsetDateTime.now(ZoneOffset.UTC)));
        log.info("Added new link with id: {}", id);
        return id;
    }
}
