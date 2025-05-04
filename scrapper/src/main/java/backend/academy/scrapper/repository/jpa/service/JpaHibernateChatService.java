package backend.academy.scrapper.repository.jpa.service;

import backend.academy.scrapper.repository.interfaces.ChatRepository;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaHibernateChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
//@Service
@RequiredArgsConstructor
public class JpaHibernateChatService { //implements ChatRepository {

    private final JpaHibernateChatRepository chatRepository;

//    @Override
    @Transactional
    public void registerChat(Long chatId) {
        if (!chatRepository.existsByChatId(chatId)) {
            ChatEntity chat = new ChatEntity();
            chat.chatId(chatId);
            chatRepository.save(chat);
        }
    }

//    @Override
    @Transactional
    public boolean deleteChat(Long chatId) {
        if (chatRepository.existsByChatId(chatId)) {
            chatRepository.deleteByChatId(chatId);
            return true;
        }
        return false;
    }

//    @Override
    @Transactional(readOnly = true)
    public List<Long> getChatIds() {
        return chatRepository.findAll().stream()
            .map(ChatEntity::chatId)
            .toList();
    }
}
