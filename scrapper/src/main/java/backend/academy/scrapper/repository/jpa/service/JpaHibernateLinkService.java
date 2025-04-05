package backend.academy.scrapper.repository.jpa.service;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateLinkRepository;
import backend.academy.scrapper.repository.model.Link;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaHibernateLinkService implements LinkRepository {

    private final JpaHibernateLinkRepository linkRepository;

    public List<Link> getLinks() {
        List<LinkEntity> links = linkRepository.findAll();
        log.info("Get all links. Count: {}", links.size());
        return links.stream()
            .map(entity -> new Link(
                entity.id(),
                entity.url(),
                entity.description(),
                entity.updateTime()
            ))
            .toList();
    }

    public OffsetDateTime getUpdateTime(Long linkId) {
        return linkRepository.findById(linkId)
            .map(LinkEntity::updateTime)
            .orElse(null);
    }

    public String getLinkById(Long linkId) {
        return linkRepository.findById(linkId)
            .map(LinkEntity::url)
            .orElse(null);
    }

    public Long getIdByUrl(String url) {
        return linkRepository.findByUrl(url)
            .map(LinkEntity::id)
            .orElse(null);
    }

    public void setUpdateTime(Long linkId, OffsetDateTime updateTime) {
        linkRepository.findById(linkId).ifPresent(link -> {
            link.updateTime(updateTime);
            linkRepository.save(link);
        });
    }

    public Long addLink(String url) {
        return linkRepository.findByUrl(url)
            .map(link -> {
                link.updateTime(OffsetDateTime.now());
                return linkRepository.save(link).id();
            })
            .orElseGet(() -> {
                LinkEntity link = new LinkEntity();
                link.url(url);
                link.description("Без изменений");
                link.updateTime(OffsetDateTime.now());
                return linkRepository.save(link).id();
            });
    }
}
