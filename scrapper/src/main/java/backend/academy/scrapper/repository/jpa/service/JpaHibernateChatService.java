package backend.academy.scrapper.repository.jpa.service;

import backend.academy.scrapper.repository.interfaces.ChatRepository;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaHibernateChatService implements ChatRepository {

    private final JpaHibernateChatRepository chatRepository;

    public void registerChat(Long chatId) {
        if (chatRepository.findByChatId(chatId).isEmpty()) {
            ChatEntity chat = new ChatEntity();
            chat.chatId(chatId);
            chatRepository.save(chat);
            log.info("Chat with id {} registered", chatId);
        }
    }

    public boolean deleteChat(Long chatId) {
        return chatRepository.findByChatId(chatId)
            .map(chat -> {
                chatRepository.delete(chat);
                log.info("Chat with id {} deleted", chatId);
                return true;
            })
            .orElse(false);
    }

    public List<Long> getChatIds() {
        return chatRepository.findAll().stream()
            .map(ChatEntity::chatId)
            .toList();
    }
}
