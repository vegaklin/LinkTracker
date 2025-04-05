package backend.academy.scrapper.repository.jpa.repository;

import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.entity.ChatLinkEntity;
import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.entity.model.ChatLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaHibernateChatLinksRepository extends JpaRepository<ChatLinkEntity, ChatLinkId> {
    List<ChatLinkEntity> findByChat(ChatEntity chat);
    List<ChatLinkEntity> findByLink(LinkEntity link);
}
