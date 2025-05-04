package backend.academy.scrapper.repository.jpa.service;

import backend.academy.scrapper.repository.interfaces.ChatLinksRepository;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.entity.ChatLinkEntity;
import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.entity.model.ChatLinkId;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateChatLinksRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateChatRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateLinkRepository;
import backend.academy.scrapper.repository.model.ChatLink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public class JpaHibernateChatLinksService implements ChatLinksRepository {

    private final JpaHibernateChatLinksRepository jpaHibernateChatLinksRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Long> getLinksForChat(Long chatRowId) {
        return jpaHibernateChatLinksRepository.findAllById_ChatId(chatRowId)
            .stream()
            .map(ChatLinkEntity::getLinkId)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChatLink getChatLinkByChatIdAndLinkId(Long chatRowId, Long linkId) {
        ChatLinkId id = new ChatLinkId(chatRowId, linkId);
        return jpaHibernateChatLinksRepository.findById(id)
            .map(entity -> new ChatLink(
                entity.getChatId(),
                entity.getLinkId(),
                entity.tags(),
                entity.filters()
            ))
            .orElse(null);
    }

    @Override
    @Transactional
    public void addLink(ChatLink chatLink) {
        ChatLinkEntity entity = new ChatLinkEntity();
        entity.id(new ChatLinkId(chatLink.chatId(), chatLink.linkId()));

        ChatEntity chat = new ChatEntity();
        chat.id(chatLink.chatId());
        LinkEntity link = new LinkEntity();
        link.id(chatLink.linkId());

        entity.chat(chat);
        entity.link(link);
        entity.tags(chatLink.tags());
        entity.filters(chatLink.filters());
        jpaHibernateChatLinksRepository.save(entity);
    }

    @Override
    @Transactional
    public boolean removeLink(Long chatRowId, Long linkId) {
        ChatLinkId id = new ChatLinkId(chatRowId, linkId);
        if (jpaHibernateChatLinksRepository.existsById(id)) {
            jpaHibernateChatLinksRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void removeChatLinks(Long chatRowId) {
        jpaHibernateChatLinksRepository.deleteAllById_ChatId(chatRowId);
    }
}
