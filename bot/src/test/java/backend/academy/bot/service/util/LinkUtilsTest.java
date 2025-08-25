package backend.academy.bot.service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LinkUtilsTest {

    @Test
    void checkFormatLinkWithTagsAndFilters() {
        // given

        LinkResponse link = new LinkResponse(1L, "https://test.ru", List.of("tag1", "tag2"), List.of("filter:filter1"));
        String expected = "Ссылка: https://test.ru\nТеги: tag1, tag2\nФильтры: filter:filter1";

        // when

        String result = LinkUtils.formatLink(link);

        // then

        assertEquals(expected, result);
    }

    @Test
    void checkFormatLinkNullLink() {
        // given-when-then

        assertEquals("Ссылка отсутствует", LinkUtils.formatLink(null));
    }

    @Test
    void checkFormatLinkNoTagsNoFilters() {
        // given

        LinkResponse link = new LinkResponse(1L, "https://test.ru", null, Collections.emptyList());
        String expected = "Ссылка: https://test.ru\nТеги: отсутствуют\nФильтры: отсутствуют";

        // when

        String result = LinkUtils.formatLink(link);

        // then

        assertEquals(expected, result);
    }

    @Test
    void checkFormatListWithItems() {
        // given

        List<String> items = List.of("item1", "item2", "item3");

        // when

        String result = LinkUtils.formatList(items);

        // then

        assertEquals("item1, item2, item3", result);
    }

    @Test
    void checkFormatListNullOrEmpty() {
        // given-when-then

        assertEquals("отсутствуют", LinkUtils.formatList(null));
        assertEquals("отсутствуют", LinkUtils.formatList(Collections.emptyList()));
    }

    @Test
    void checkIsEmptyLinks() {
        // given

        ListLinksResponse emptyLinks = new ListLinksResponse(Collections.emptyList(), 0);
        ListLinksResponse nonEmptyLinks =
                new ListLinksResponse(List.of(new LinkResponse(1L, "https://test.ru", null, null)), 1);

        // when-then

        assertTrue(LinkUtils.isEmptyLinks(null));
        assertTrue(LinkUtils.isEmptyLinks(emptyLinks));
        assertFalse(LinkUtils.isEmptyLinks(nonEmptyLinks));
    }

    @Test
    void checkSplitFiltersAndTags() {
        // given

        String message = "tag1 tag2   tag3  ";

        // when

        List<String> result = LinkUtils.splitFiltersAndTags(message);

        // then

        assertEquals(List.of("tag1", "tag2", "tag3"), result);
    }

    @Test
    void checkSplitFiltersAndTagsEmpty() {
        // given-when-then

        assertTrue(LinkUtils.splitFiltersAndTags("").isEmpty());
        assertEquals(List.of(""), LinkUtils.splitFiltersAndTags("   "));
    }

    @Test
    void checkIsAnyMatchLinks() {
        // given

        ListLinksResponse links = new ListLinksResponse(
                List.of(
                        new LinkResponse(1L, "https://example.ru", null, null),
                        new LinkResponse(2L, "https://test.ru", null, null)),
                1);

        // when-then

        assertTrue(LinkUtils.isAnyMatchLinks(links, "https://example.ru"));
        assertFalse(LinkUtils.isAnyMatchLinks(links, "https://unknown.ru"));
        assertFalse(LinkUtils.isAnyMatchLinks(null, "https://example.ru"));
        assertFalse(LinkUtils.isAnyMatchLinks(new ListLinksResponse(null, 0), "https://example.ru"));
        assertFalse(LinkUtils.isAnyMatchLinks(new ListLinksResponse(Collections.emptyList(), 0), "https://example.ru"));
    }

    @Test
    void checkIsCorrectParts() {
        // given-when-then

        assertTrue(LinkUtils.isCorrectParts(new String[] {"part1"}));
        assertFalse(LinkUtils.isCorrectParts(new String[] {"part1", "part2"}));
        assertFalse(LinkUtils.isCorrectParts(new String[] {"part1", "part2", "part3"}));
    }
}
