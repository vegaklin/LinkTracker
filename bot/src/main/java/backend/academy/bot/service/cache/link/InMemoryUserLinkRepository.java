package backend.academy.bot.service.cache.link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class InMemoryUserLinkRepository implements UserLinkRepository {
    private final Map<Long, String> userLinks = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> userTags = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> userFilters = new ConcurrentHashMap<>();

    @Override
    public void setLink(long chatId, String link) {
        userLinks.put(chatId, link);
        log.info("Set link for chatId: {} to {}", chatId, link);
    }

    @Override
    public String getLink(long chatId) {
        String link = userLinks.get(chatId);
        if (link != null) {
            log.info("Get link for chatId: {}", chatId);
        } else {
            log.info("No link found for chatId: {}", chatId);
        }
        return link;
    }

    @Override
    public void setTags(long chatId, List<String> tags) {
        userTags.put(chatId, tags);
        log.info("Set tags for chatId: {} to {}", chatId, tags);
    }

    @Override
    public List<String> getTags(long chatId) {
        List<String> tags = userTags.getOrDefault(chatId, new ArrayList<>());
        if (!tags.isEmpty()) {
            log.info("Get tags for chatId: {}", chatId);
        } else {
            log.info("No tags found for chatId: {}", chatId);
        }
        return tags;
    }

    @Override
    public void setFilters(long chatId, List<String> filters) {
        userFilters.put(chatId, filters);
        log.info("Set filters for chatId: {} to {}", chatId, filters);
    }

    @Override
    public List<String> getFilters(long chatId) {
        List<String> filters = userFilters.getOrDefault(chatId, new ArrayList<>());
        if (!filters.isEmpty()) {
            log.info("Get filters for chatId: {}", chatId);
        } else {
            log.warn("No info found for chatId: {}", chatId);
        }
        return filters;
    }

    @Override
    public void clear(long chatId) {
        userLinks.remove(chatId);
        userTags.remove(chatId);
        userFilters.remove(chatId);
        log.info("Cleared data for chatId: {}", chatId);
    }
}
