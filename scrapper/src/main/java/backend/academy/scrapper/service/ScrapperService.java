package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.repository.chat.ChatLinksRepository;
import backend.academy.scrapper.repository.chat.ChatRepository;
import backend.academy.scrapper.repository.link.LinkRepository;
import backend.academy.scrapper.repository.link.model.Link;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperService {

    private final UpdateCheckService linkService;

    private final ChatRepository chatRepository;
    private final ChatLinksRepository chatLinksRepository;
    private final LinkRepository linkRepository;

    public void registerChat(Long chatId) {
        chatRepository.registerChat(chatId);
    }

    public void deleteChat(Long chatId) {
        chatLinksRepository.removeChatLinks(chatId);
        chatRepository.deleteChat(chatId);
    }

    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {
        Link link = new Link(
                addLinkRequest.link(),
                addLinkRequest.tags(),
                addLinkRequest.filters(),
                linkService.checkUpdate(addLinkRequest.link()).block());
        LinkResponse linkResponse = linkRepository.addLink(link);
        chatLinksRepository.addLink(chatId, linkResponse.id());
        return linkResponse;
    }

    public LinkResponse removeLink(Long chatId, RemoveLinkRequest request) {
        Long linkId = linkRepository.getIdByUrl(request.link());
        if (linkId == null) {
            throw new LinkNotFoundException("Ссылка не найдена для URL: " + request.link());
        }
        if (!chatLinksRepository.removeLink(chatId, linkId)) {
            throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена для чата с id " + chatId);
        }
        LinkResponse linkResponse = linkRepository.getLinkById(linkId);
        if (linkResponse == null) {
            throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена");
        }
        return linkResponse;
    }

    public ListLinksResponse getAllLinks(Long chatId) {
        Set<Long> linkIds = chatLinksRepository.getLinksForChat(chatId);
        List<LinkResponse> links = linkIds.stream()
                .map(linkId -> {
                    LinkResponse link = linkRepository.getLinkById(linkId);
                    if (link == null) {
                        throw new LinkNotFoundException("Ссылка с id " + linkId + " не найдена");
                    }
                    return link;
                })
                .toList();
        return new ListLinksResponse(links, links.size());
    }
}
