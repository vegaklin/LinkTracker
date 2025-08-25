package backend.academy.bot.service.util;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import java.util.Arrays;
import java.util.List;

public class LinkUtils {

    public static String formatLink(LinkResponse link) {
        if (link == null) {
            return "Ссылка отсутствует";
        }
        String url = link.url();
        String tags = formatList(link.tags());
        String filters = formatList(link.filters());

        return "Ссылка: " + url + "\nТеги: " + tags + "\nФильтры: " + filters;
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
        if (message == null || message.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(message.trim().split("\\s+"));
    }

    public static boolean isAnyMatchLinks(ListLinksResponse links, String message) {
        return links != null
                && links.links() != null
                && links.links().stream().anyMatch(link -> link.url().equals(message));
    }

    public static boolean isCorrectParts(String[] parts) {
        return parts.length < 2;
    }
}
