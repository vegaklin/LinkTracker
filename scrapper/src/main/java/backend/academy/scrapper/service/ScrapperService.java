package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.repository.chat.ChatLinksRepository;
import backend.academy.scrapper.repository.chat.ChatRepository;
import backend.academy.scrapper.repository.chat.model.ChatLink;
import backend.academy.scrapper.repository.link.LinkRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        chatLinksRepository.removeChatLinks(chatId);
        if (!chatRepository.deleteChat(chatId)) {
            log.error("Chat not found for chatId: {}", chatId.toString());
            throw new ChatNotFoundException("Чат не найден с id: " + chatId);
        }
    }

    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {
        log.info("Adding link for chatId {}: {}", chatId, addLinkRequest.link());

        Long linkId = linkRepository.addLink(addLinkRequest.link());
        chatLinksRepository.addLink(new ChatLink(chatId, linkId, addLinkRequest.tags(), addLinkRequest.filters()));

        ChatLink chatLink = chatLinksRepository.getChatLinksByCharIdAndLinkId(chatId, linkId);

        return new LinkResponse(chatLink.link_id(), addLinkRequest.link(), chatLink.tags(), chatLink.tags());
    }

    public LinkResponse removeLink(Long chatId, RemoveLinkRequest request) {
        log.info("Removing link for chatId {}: {}", chatId, request.link());
        Long linkId = linkRepository.getIdByUrl(request.link());


        if (linkId == null) {
            log.error("Link not found for url: {}", request.link());
            throw new LinkNotFoundException("Ссылка не найдена для URL: " + request.link());
        }

        String link = linkRepository.getLinkById(linkId);
        if (link == null) {
            log.error("Link with id {} not found while removing", linkId);
            throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена");
        }

        ChatLink chatLink = chatLinksRepository.getChatLinksByCharIdAndLinkId(chatId, linkId);

        if (!chatLinksRepository.removeLink(chatId, linkId)) {
            log.error("Link with id {} not found for chat id {}", linkId, chatId.toString());
            throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена для чата с id " + chatId);
        }

        log.info("Link with ID {} removed successfully", linkId);
        return new LinkResponse(chatLink.link_id(), link, chatLink.tags(), chatLink.tags());
    }

    public ListLinksResponse getAllLinks(Long chatId) {
        log.info("Get all links for chat id: {}", chatId.toString());
        List<Long> linkIds = chatLinksRepository.getLinksForChat(chatId);
        List<LinkResponse> links = linkIds.stream()
                .map(linkId -> {
                    String link = linkRepository.getLinkById(linkId);

                    if (link == null) {
                        log.error("Link with id {} not found", linkId);
                        throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена");
                    }

                    ChatLink chatLink = chatLinksRepository.getChatLinksByCharIdAndLinkId(chatId, linkId);

                    return new LinkResponse(chatLink.link_id(), link, chatLink.tags(), chatLink.tags());
                })
                .toList();
        log.info("Found {} links for chat id {}", links.size(), chatId.toString());
        return new ListLinksResponse(links, links.size());
    }
}
