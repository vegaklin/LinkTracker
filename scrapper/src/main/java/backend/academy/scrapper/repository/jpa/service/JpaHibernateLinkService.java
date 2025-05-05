package backend.academy.scrapper.repository.jpa.service;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateLinkRepository;
import backend.academy.scrapper.repository.model.Link;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public class JpaHibernateLinkService implements LinkRepository {

    private final JpaHibernateLinkRepository jpaHibernateLinkRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Link> getLinks() {
        List<LinkEntity> links = jpaHibernateLinkRepository.findAll();
        return links.stream()
            .map(entity -> new Link(
                entity.id(),
                entity.url(),
                entity.description(),
                entity.updateTime()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OffsetDateTime getUpdateTime(Long linkId) {
        return jpaHibernateLinkRepository.findById(linkId)
            .map(LinkEntity::updateTime)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public String getLinkById(Long linkId) {
        return jpaHibernateLinkRepository.findById(linkId)
            .map(LinkEntity::url)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getIdByUrl(String url) {
        return jpaHibernateLinkRepository.findByUrl(url)
            .map(LinkEntity::id)
            .orElse(null);
    }

    @Override
    @Transactional
    public void setUpdateTime(Long linkId, OffsetDateTime updateTime) {
        jpaHibernateLinkRepository.findById(linkId).ifPresent(link -> {
            link.updateTime(updateTime);
            jpaHibernateLinkRepository.save(link);
        });
    }

    @Override
    @Transactional
    public void setDescription(Long linkId, String description) {
        jpaHibernateLinkRepository.findById(linkId).ifPresent(link -> {
            link.description(description);
            jpaHibernateLinkRepository.save(link);
        });
    }

    @Override
    @Transactional
    public Long addLink(String url) {
        return jpaHibernateLinkRepository.findByUrl(url)
            .map(link -> {
                link.updateTime(OffsetDateTime.now());
                return jpaHibernateLinkRepository.save(link).id();
            })
            .orElseGet(() -> {
                LinkEntity link = new LinkEntity();
                link.url(url);
                link.description("Без изменений");
                link.updateTime(OffsetDateTime.now());
                return jpaHibernateLinkRepository.save(link).id();
            });
    }
}
