package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ChatLinkEntity;
import backend.academy.scrapper.repository.jpa.entity.model.ChatLinkId;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHibernateChatLinksRepository extends JpaRepository<ChatLinkEntity, ChatLinkId> {
    List<ChatLinkEntity> findAllById_ChatId(Long chatId);

    void deleteById(@NotNull ChatLinkId id);

    void deleteAllById_ChatId(Long chatId);
}
