package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.entity.ChatLinkEntity;
import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.entity.model.ChatLinkId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface JpaHibernateChatLinksRepository extends JpaRepository<ChatLinkEntity, ChatLinkId> {
    List<ChatLinkEntity> findAllById_ChatId(Long chatId);
    void deleteById(@NotNull ChatLinkId id);
    void deleteAllById_ChatId(Long chatId);

    @NotNull
    Optional<ChatLinkEntity> findById(@NotNull ChatLinkId id);
}
