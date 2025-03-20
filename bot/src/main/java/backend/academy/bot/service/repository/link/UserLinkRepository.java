package backend.academy.bot.service.repository.link;

import java.util.List;

public interface UserLinkRepository {
    void setLink(long chatId, String link);

    String getLink(long chatId);

    void setTags(long chatId, List<String> tags);

    List<String> getTags(long chatId);

    void setFilters(long chatId, List<String> filters);

    List<String> getFilters(long chatId);

    void clear(long chatId);
}
