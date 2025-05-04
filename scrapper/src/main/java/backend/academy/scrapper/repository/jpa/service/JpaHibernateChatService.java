package backend.academy.scrapper.repository.jpa.service;

import backend.academy.scrapper.repository.interfaces.ChatRepository;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
        return jpaHibernateChatRepository.findAll().stream()
            .map(ChatEntity::chatId)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long findIdByChatId(Long chatId) {
        return jpaHibernateChatRepository.findByChatId(chatId)
            .map(ChatEntity::id)
            .orElse(null);
    }
}
