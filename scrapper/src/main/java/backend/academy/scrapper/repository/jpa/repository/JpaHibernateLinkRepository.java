package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHibernateLinkRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUrl(String url);
}
