package backend.academy.bot.service.util;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import java.util.Arrays;
import java.util.List;

public class LinkUtils {

    public static String formatLink(LinkResponse link) {
        String url = link.url();
        String tags = formatList(link.tags());
        String filters = formatList(link.filters());
        return String.format(
                """
               Ссылка: %s
               Теги: %s
               Фильтры: %s""",
                url, tags, filters);
    }

    public static String formatList(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "отсутствуют";
        }
        return String.join(", ", items);
    }

    public static boolean isEmptyLinks(ListLinksResponse links) {
        return links == null || links.links().isEmpty();
    }

    public static List<String> splitFiltersAndTags(String message) {
        return Arrays.asList(message.trim().split(" "));
    }

    public static boolean isAnyMatchLinks(ListLinksResponse links, String message) {
        return links != null
                && links.links() != null
                && links.links().stream().anyMatch(link -> link.url().equals(message));
    }
}
