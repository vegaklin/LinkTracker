package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperService {

    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;

    public void registerChat(Long chatId) {
        chatRepository.registerChat(chatId);
    }

    public void deleteChat(Long chatId) {
        chatRepository.deleteChat(chatId);
    }

    public LinkResponse addLink(Long chatId, AddLinkRequest request) {
        System.out.println("add link");
        if (!chatRepository.existsById(chatId)) {
            throw new RuntimeException("Чат не найден");
        }

        //        if (linkRepository.existsByUrlAndChatId(request.link(), chatId)) {
        //            throw new RuntimeException("Ссылка уже отслеживается");
        //        }

        LinkResponse link = new LinkResponse(
                System.currentTimeMillis(), // Генерация ID (временное решение)
                request.link(),
                request.tags(),
                request.filters());
        linkRepository.addLink(chatId, link);
        System.out.println("added link");
        return link;
    }

    public LinkResponse removeLink(Long chatId, RemoveLinkRequest request) {
        if (!chatRepository.existsById(chatId)) {
            throw new RuntimeException("Чат не найден");
        }

        LinkResponse link = linkRepository.findAllLinksByChatId(chatId).stream()
                .filter(l -> l.url().equals(request.link()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ссылка не найдена"));

        linkRepository.removeLink(chatId, request.link());
        return link;
    }

    public ListLinksResponse getAllLinks(Long chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new RuntimeException("Чат не найден");
        }

        List<LinkResponse> links = linkRepository.findAllLinksByChatId(chatId);
        return new ListLinksResponse(links, links.size());
    }
}
