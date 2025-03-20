package backend.academy.bot.service.repository.link;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserLinkRepository implements UserLinkRepository {
    private final Map<Long, String> userLinks = new HashMap<>();
    private final Map<Long, List<String>> userTags = new HashMap<>();
    private final Map<Long, List<String>> userFilters = new HashMap<>();

    @Override
    public void setLink(long chatId, String link) {
        userLinks.put(chatId, link);
    }

    @Override
    public String getLink(long chatId) {
        return userLinks.get(chatId);
    }

    @Override
    public void setTags(long chatId, List<String> tags) {
        userTags.put(chatId, tags);
    }

    @Override
    public List<String> getTags(long chatId) {
        return userTags.getOrDefault(chatId, new ArrayList<>());
    }

    @Override
    public void setFilters(long chatId, List<String> filters) {
        userFilters.put(chatId, filters);
    }

    @Override
    public List<String> getFilters(long chatId) {
        return userFilters.getOrDefault(chatId, new ArrayList<>());
    }

    @Override
    public void clear(long chatId) {
        userLinks.remove(chatId);
        userTags.remove(chatId);
        userFilters.remove(chatId);
    }
}
