package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.repository.chat.ChatLinksRepository;
import backend.academy.scrapper.repository.chat.ChatRepository;
import backend.academy.scrapper.repository.link.LinkRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperService {

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
        LinkResponse linkResponse = linkRepository.addLink(addLinkRequest);
        chatLinksRepository.addLink(chatId, linkResponse.id());
        return linkResponse;
    }

    public LinkResponse removeLink(Long chatId, RemoveLinkRequest request) {
        Long linkId = linkRepository.getIdByUrl(request.link());
        chatLinksRepository.removeLink(chatId, linkId);
        return linkRepository.getLinkById(linkId);
    }

    public ListLinksResponse getAllLinks(Long chatId) {
        Set<Long> linkIds = chatLinksRepository.getLinksForChat(chatId);
        List<LinkResponse> links = linkIds.stream()
            .map(linkRepository::getLinkById)
            .toList();
        return new ListLinksResponse(links, links.size());
    }
}
