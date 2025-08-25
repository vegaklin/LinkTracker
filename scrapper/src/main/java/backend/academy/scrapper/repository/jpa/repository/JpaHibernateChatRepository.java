package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHibernateChatRepository extends JpaRepository<ChatEntity, Long> {
    boolean existsByChatId(Long chatId);

    void deleteByChatId(Long chatId);

    Optional<ChatEntity> findByChatId(Long chatId);
}
