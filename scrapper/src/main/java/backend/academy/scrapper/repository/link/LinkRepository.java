package backend.academy.scrapper.repository.link;

import java.util.HashMap;
import java.util.Map;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.repository.link.model.Link;
import org.springframework.stereotype.Repository;

@Repository
public class LinkRepository {

    private final Map<Long, Link> links = new HashMap<>();

    private Long idCounter = 1L;

    public Map<Long, Link> getLinks() {
        return links;
    }

    public LinkResponse addLink(Link link) {
        Long id = idCounter++;
        links.put(id, addLinkRequest);
        return new LinkResponse(id, addLinkRequest.link(), addLinkRequest.tags(), addLinkRequest.filters());
    }

    public LinkResponse getLinkById(Long linkId) {
        Link addLinkRequest = links.get(linkId);
        if (addLinkRequest != null) {
            return new LinkResponse(linkId, addLinkRequest.link(), addLinkRequest.tags(), addLinkRequest.filters());
        } else {
            throw new LinkNotFoundException("Ссылка не найдена для id: " + linkId);
        }
    }

    public Long getIdByUrl(String url) {
        return links.entrySet().stream()
            .filter(entry -> entry.getValue().link().equals(url))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new LinkNotFoundException("Ссылка не найдена для URL: " + url));
    }
}
