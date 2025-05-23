package backend.academy.scrapper.repository.jpa.service;

import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateChatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public class JpaHibernateChatService implements ChatRepository {

    private final JpaHibernateChatRepository jpaHibernateChatRepository;

    @Override
    @Transactional
    public void registerChat(Long chatId) {
        if (!jpaHibernateChatRepository.existsByChatId(chatId)) {
            ChatEntity chat = new ChatEntity();
            chat.chatId(chatId);
            jpaHibernateChatRepository.save(chat);
        }
    }

    @Override
    @Transactional
    public boolean deleteChat(Long chatId) {
        if (jpaHibernateChatRepository.existsByChatId(chatId)) {
            jpaHibernateChatRepository.deleteByChatId(chatId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getChatIds() {
        return jpaHibernateChatRepository.findAll().stream().map(ChatEntity::id).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long findIdByChatId(Long chatId) {
        return jpaHibernateChatRepository
                .findByChatId(chatId)
                .map(ChatEntity::id)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Long findChatIdById(Long chatRowId) {
        return jpaHibernateChatRepository
                .findById(chatRowId)
                .map(ChatEntity::chatId)
                .orElse(null);
    }
}
