package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaHibernateChatRepository extends JpaRepository<ChatEntity, Long> {
    boolean existsByChatId(Long chatId);
    Optional<ChatEntity> findByChatId(Long chatId);
    void deleteByChatId(Long chatId);
}
