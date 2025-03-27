package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.repository.chat.ChatLinksRepository;
import backend.academy.scrapper.repository.chat.ChatRepository;
import backend.academy.scrapper.repository.link.LinkRepository;
import backend.academy.scrapper.repository.link.model.Link;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapperService {

    private final UpdateCheckService updateCheckService;

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
        Link link = new Link(
                addLinkRequest.link(),
                addLinkRequest.tags(),
                addLinkRequest.filters(),
                updateCheckService.checkUpdate(addLinkRequest.link()).block());
        LinkResponse linkResponse = linkRepository.addLink(link);
        chatLinksRepository.addLink(chatId, linkResponse.id());
        return linkResponse;
    }

    public LinkResponse removeLink(Long chatId, RemoveLinkRequest request) {
        log.info("Removing link for chatId {}: {}", chatId, request.link());
        Long linkId = linkRepository.getIdByUrl(request.link());
        if (linkId == null) {
            log.error("Link not found for url: {}", request.link());
            throw new LinkNotFoundException("Ссылка не найдена для URL: " + request.link());
        }
        if (!chatLinksRepository.removeLink(chatId, linkId)) {
            log.error("Link with id {} not found for chat id {}", linkId, chatId.toString());
            throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена для чата с id " + chatId);
        }
        LinkResponse linkResponse = linkRepository.getLinkById(linkId);
        if (linkResponse == null) {
            log.error("Link with id {} not found while removing", linkId);
            throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена");
        }
        log.info("Link with ID {} removed successfully", linkId);
        return linkResponse;
    }

    public ListLinksResponse getAllLinks(Long chatId) {
        log.info("Get all links for chat id: {}", chatId.toString());
        Set<Long> linkIds = chatLinksRepository.getLinksForChat(chatId);
        List<LinkResponse> links = linkIds.stream()
                .map(linkId -> {
                    LinkResponse link = linkRepository.getLinkById(linkId);
                    if (link == null) {
                        log.error("Link with id {} not found", linkId);
                        throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена");
                    }
                    return link;
                })
                .toList();
        log.info("Found {} links for chat id {}", links.size(), chatId.toString());
        return new ListLinksResponse(links, links.size());
    }
}
