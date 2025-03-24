package backend.academy.bot.service.cache.link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryUserLinkRepositoryTest {

    private UserLinkRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserLinkRepository();
    }

    @Test
    void checkSetLinkValidChatIdAndLinkCorrectGet() {
        // given

        long chatId = 1L;
        String expectedLink = "http://test.ru";

        // when

        repository.setLink(chatId, expectedLink);
        String actualLink = repository.getLink(chatId);

        // then

        assertEquals(expectedLink, actualLink);
    }

    @Test
    void checkGetLinkChatIdNotExistsReturnNull() {
        // given

        long chatId = 1L;

        // when

        String link = repository.getLink(chatId);

        // then

        assertNull(link);
    }

    @Test
    void checkSetTagsValidChatIdAndTagsGet() {
        // given

        long chatId = 1L;
        List<String> expectedTags = List.of("tag1", "tag2");

        // when

        repository.setTags(chatId, expectedTags);
        List<String> actualTags = repository.getTags(chatId);
        // then

        assertEquals(expectedTags, actualTags);
    }

    @Test
    void checkGetTagsChatIdNotExistsReturnEmptyList() {
        // given

        long chatId = 1L;

        // when

        List<String> tags = repository.getTags(chatId);

        // then

        assertTrue(tags.isEmpty());
    }

    @Test
    void checkSetFiltersValidChatIdAndFiltersGet() {
        // given

        long chatId = 1L;
        List<String> expectedFilters = List.of("filter1", "filter2");

        // when

        repository.setFilters(chatId, expectedFilters);
        List<String> actualFilters = repository.getFilters(chatId);

        // then

        assertEquals(expectedFilters, actualFilters);
    }

    @Test
    void checkGetFiltersChatIdNotExistsReturnEmptyList() {
        // given

        long chatId = 1L;

        // when

        List<String> filters = repository.getFilters(chatId);

        // then

        assertTrue(filters.isEmpty());
    }

    @Test
    void checkClearChatIdWithDataRemovesAllData() {
        // given

        long chatId = 1L;
        repository.setLink(chatId, "http://test.ru");
        repository.setTags(chatId, List.of("tag1"));

        repository.setFilters(chatId, List.of("filter1"));

        // when

        repository.clear(chatId);

        // then

        assertNull(repository.getLink(chatId));
        assertTrue(repository.getTags(chatId).isEmpty());
        assertTrue(repository.getFilters(chatId).isEmpty());
    }

    @Test
    void checkSetLinkOverride() {
        // given

        long chatId = 1L;
        repository.setLink(chatId, "http://old.ru");

        String expectedNewLink = "http://new.ru";

        // when

        repository.setLink(chatId, expectedNewLink);
        String actualNewLink = repository.getLink(chatId);

        // then

        assertEquals(expectedNewLink, actualNewLink);
    }

    @Test
    void checkSetTagsOverride() {
        // given

        long chatId = 1L;
        repository.setTags(chatId, List.of("oldTag"));

        List<String> expectedNewTags = List.of("newTag1", "newTag2");

        // when

        repository.setTags(chatId, expectedNewTags);
        List<String> actualNewTags = repository.getTags(chatId);

        // then

        assertEquals(expectedNewTags, actualNewTags);
    }

    @Test
    void checkSetFiltersOverride() {
        // given

        long chatId = 1L;
        repository.setFilters(chatId, List.of("oldFilter"));

        List<String> expectedNewFilters = List.of("newFilter1", "newFilter2");

        // when

        repository.setFilters(chatId, expectedNewFilters);
        List<String> actualNewFilters = repository.getFilters(chatId);
        // then

        assertEquals(expectedNewFilters, actualNewFilters);
    }
}
