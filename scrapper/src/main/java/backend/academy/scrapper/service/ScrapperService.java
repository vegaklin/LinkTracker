package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.repository.ChatLinksRepository;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.model.ChatLink;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapperService {

    private final ChatRepository chatRepository;
    private final ChatLinksRepository chatLinksRepository;
    private final LinkRepository linkRepository;

    public void registerChat(Long chatId) {
        log.info("Registering chat with chatId: {}", chatId.toString());

        chatRepository.registerChat(chatId);
    }

    public void deleteChat(Long chatId) {
        log.info("Deleting chat with chatId: {}", chatId.toString());

        if (!chatRepository.deleteChat(chatId)) {
            log.error("Chat not found for chatId: {}", chatId.toString());
            throw new ChatNotFoundException("Чат не найден с id: " + chatId);
        }
    }

    @Transactional
    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {
        log.info("Adding link for chatId {}: {}", chatId, addLinkRequest.link());

        Long chatRowId = chatRepository.findIdByChatId(chatId);
        if (chatRowId == null) {
            log.error("Id not found for chatId while addLink: {}", chatId);
            throw new ChatNotFoundException("Id не найдена для chatId: " + chatId);
        }

        Long linkId = linkRepository.addLink(addLinkRequest.link());
        chatLinksRepository.addLink(new ChatLink(chatRowId, linkId, addLinkRequest.tags(), addLinkRequest.filters()));

        ChatLink chatLink = chatLinksRepository.getChatLinkByChatIdAndLinkId(chatRowId, linkId);
        return new LinkResponse(chatLink.linkId(), addLinkRequest.link(), chatLink.tags(), chatLink.filters());
    }

    @Transactional
    public LinkResponse removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        log.info("Removing link for chatId {}: {}", chatId, removeLinkRequest.link());

        Long linkId = linkRepository.getIdByUrl(removeLinkRequest.link());
        if (linkId == null) {
            log.error("Link not found for url: {}", removeLinkRequest.link());
            throw new LinkNotFoundException("Ссылка не найдена для URL: " + removeLinkRequest.link());
        }

        Long chatRowId = chatRepository.findIdByChatId(chatId);
        if (chatRowId == null) {
            log.error("Id not found for chatId while removeLink: {}", chatId);
            throw new ChatNotFoundException("Id не найдена для chatId: " + chatId);
        }

        ChatLink chatLink = chatLinksRepository.getChatLinkByChatIdAndLinkId(chatRowId, linkId);
        if (!chatLinksRepository.removeLink(chatRowId, linkId)) {
            log.error("Link with id {} not found for chat id {}", linkId, chatId.toString());
            throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена для чата с id " + chatId);
        }

        log.info("Link with ID {} removed successfully", linkId);
        return new LinkResponse(chatLink.linkId(), removeLinkRequest.link(), chatLink.tags(), chatLink.tags());
    }

    @Transactional
    public ListLinksResponse getAllLinks(Long chatId) {
        log.info("Get all links for chat id: {}", chatId.toString());

        Long chatRowId = chatRepository.findIdByChatId(chatId);
        if (chatRowId == null) {
            log.error("Id not found for chatId while getAllLinks: {}", chatId);
            throw new ChatNotFoundException("Id не найдена для chatId: " + chatId);
        }

        List<Long> linkIds = chatLinksRepository.getLinksForChat(chatRowId);
        List<LinkResponse> links = linkIds.stream()
                .map(linkId -> {
                    ChatLink chatLink = chatLinksRepository.getChatLinkByChatIdAndLinkId(chatRowId, linkId);
                    String link = linkRepository.getLinkById(linkId);
                    return new LinkResponse(chatLink.linkId(), link, chatLink.tags(), chatLink.filters());
                })
                .toList();

        log.info("Found {} links for chat id {}", links.size(), chatId.toString());
        return new ListLinksResponse(links, links.size());
    }
}
