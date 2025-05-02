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
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaHibernateChatLinksService implements ChatLinksRepository {

    private final JpaHibernateChatRepository chatRepository;
    private final JpaHibernateLinkRepository linkRepository;
    private final JpaHibernateChatLinksRepository chatLinkRepository;

    public List<Long> getLinksForChat(Long chatId) {
        return chatRepository.findByChatId(chatId)
            .map(chat -> chatLinkRepository.findByChat(chat).stream()
                .map(chatLink -> chatLink.link().id())
                .toList())
            .orElse(List.of());
    }

    public ChatLink getChatLinkByChatIdAndLinkId(Long chatId, Long linkId) {
        Optional<ChatEntity> chatOpt = chatRepository.findByChatId(chatId);
        if (chatOpt.isEmpty()) return null;

        Long chatInternalId = chatOpt.get().id();
        ChatLinkId id = new ChatLinkId(chatInternalId, linkId);
        return chatLinkRepository.findById(id)
            .map(entity -> new ChatLink(
                entity.chat().chatId(),   // внешний chatId
                entity.link().id(),       // linkId
                entity.tags(),
                entity.filters()
            ))
            .orElse(null);
    }

    public void addLink(ChatLink chatLinks) {
//        Long chatId, Long linkId, List<String> tags, List<String> filters;

        ChatEntity chat = chatRepository.findByChatId(chatLinks.chat_id()).orElseThrow();
        LinkEntity link = linkRepository.findById(chatLinks.link_id()).orElseThrow();

        ChatLinkId id = new ChatLinkId(chat.id(), link.id());

        if (chatLinkRepository.existsById(id)) {
            return; // Уже существует
        }

        ChatLinkEntity chatLink = new ChatLinkEntity();
        chatLink.id(id);
        chatLink.chat(chat);
        chatLink.link(link);
        chatLink.tags(chatLinks.tags());
        chatLink.filters(chatLinks.filters());
        chatLinkRepository.save(chatLink);
    }

    public boolean removeLink(Long chatId, Long linkId) {
        return chatRepository.findByChatId(chatId)
            .map(chat -> {
                ChatLinkId id = new ChatLinkId(chat.id(), linkId);
                if (chatLinkRepository.existsById(id)) {
                    chatLinkRepository.deleteById(id);
                    return true;
                }
                return false;
            })
            .orElse(false);
    }

    public void removeChatLinks(Long chatId) {
        chatRepository.findByChatId(chatId).ifPresent(chat -> {
            List<ChatLinkEntity> links = chatLinkRepository.findByChat(chat);
            chatLinkRepository.deleteAll(links);
        });
    }
}
