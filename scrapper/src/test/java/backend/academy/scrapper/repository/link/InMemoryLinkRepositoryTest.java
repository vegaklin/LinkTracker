package backend.academy.scrapper.repository.link;

import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.repository.link.model.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryLinkRepositoryTest {

    private InMemoryLinkRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLinkRepository();
    }

    @Test
    void checkAddLink() {
        // given

        Link link = new Link("https://test.ru", List.of("tag1"), List.of("filter:filter1"), OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));

        // when

        LinkResponse response = repository.addLink(link);

        // then

        assertEquals(1L, response.id());
        assertEquals("https://test.ru", response.url());
        assertEquals(List.of("tag1"), response.tags());
        assertEquals(List.of("filter:filter1"), response.filters());
        assertEquals(1, repository.getLinks().size());
        assertTrue(repository.getLinks().containsKey(1L));
    }

    @Test
    void checkGetLinksEmpty() {
        // given-when

        Map<Long, Link> links = repository.getLinks();

        // then

        assertTrue(links.isEmpty());
    }

    @Test
    void checkGetUpdateTime() {
        // given

        Link link = new Link("https://test.ru", List.of("tag1"), List.of("filter:filter1"), OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        repository.addLink(link);

        // when

        OffsetDateTime time = repository.getUpdateTime(1L);

        // then

        assertEquals(OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC), time);
    }

    @Test
    void checkSetUpdateTime() {
        // given

        Link link = new Link("https://test.ru", List.of("tag1"), List.of("filter:filter1"), OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        repository.addLink(link);
        OffsetDateTime newTime = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC).plusDays(1);

        // when

        repository.setUpdateTime(1L, newTime);

        // then

        assertEquals(newTime, repository.getUpdateTime(1L));
        Link updatedLink = repository.getLinks().get(1L);
        assertEquals("https://test.ru", updatedLink.url());
        assertEquals(List.of("tag1"), updatedLink.tags());
        assertEquals(List.of("filter:filter1"), updatedLink.filters());
    }

    @Test
    void checkGetLinkByIdExisting() {
        // given

        Link link = new Link("https://test.ru", List.of("tag1"), List.of("filter:filter1"), OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        repository.addLink(link);

        // when

        LinkResponse response = repository.getLinkById(1L);

        // then

        assertEquals(1L, response.id());
        assertEquals("https://test.ru", response.url());
        assertEquals(List.of("tag1"), response.tags());
        assertEquals(List.of("filter:filter1"), response.filters());
    }

    @Test
    void checkGetLinkByIdNonExisting() {
        // given-when

        LinkResponse response = repository.getLinkById(999L);

        // then

        assertNull(response);
    }

    @Test
    void checkGetIdByUrlExisting() {
        // given

        Link link = new Link("https://test.ru", List.of("tag1"), List.of("filter:filter1"), OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        repository.addLink(link);

        // when

        Long id = repository.getIdByUrl("https://test.ru");

        // then

        assertEquals(1L, id);
    }

    @Test
    void checkGetIdByUrlNonExisting() {
        // given-when

        Long id = repository.getIdByUrl("https://test.ru");

        // then

        assertNull(id);
    }
}
