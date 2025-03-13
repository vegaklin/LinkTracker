package backend.academy.scrapper.repository;

import backend.academy.scrapper.dto.LinkResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkRepository {
    private final Map<Long, List<LinkResponse>> userLinks = new HashMap<>();

    public void addLink(Long chatId, LinkResponse link) {
        userLinks.computeIfAbsent(chatId, k -> new ArrayList<>()).add(link);
    }

    public void removeLink(Long chatId, String url) {
        userLinks.computeIfPresent(chatId, (k, links) -> {
            links.removeIf(link -> link.url().equals(url));
            return links;
        });
    }

    public List<LinkResponse> findAllLinksByChatId(Long chatId) {
        return userLinks.getOrDefault(chatId, List.of());
    }

    public boolean existsByUrlAndChatId(String url, Long chatId) {
        return userLinks.getOrDefault(chatId, List.of())
            .stream()
            .anyMatch(link -> link.url().equals(url));
    }
}
