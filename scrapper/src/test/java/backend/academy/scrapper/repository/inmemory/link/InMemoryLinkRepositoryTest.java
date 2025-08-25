package backend.academy.scrapper.repository.inmemory.link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.repository.inmemory.InMemoryLinkRepository;
import backend.academy.scrapper.repository.model.Link;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryLinkRepositoryTest {

    private InMemoryLinkRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLinkRepository();
    }

    @Test
    void checkAddLink() {
        // given

        Link link = new Link(
                1L, "https://test.ru", "description", OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));

        // when

        Link response = repository.addLink(link);

        // then

        assertEquals(1L, response.id());
        assertEquals("https://test.ru", response.url());
        assertEquals("description", response.description());
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

        Link link = new Link(
                1L, "https://test.ru", "description", OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        repository.addLink(link);

        // when

        OffsetDateTime time = repository.getUpdateTime(1L);

        // then

        assertEquals(OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC), time);
    }

    @Test
    void checkSetUpdateTime() {
        // given

        Link link = new Link(
                1L, "https://test.ru", "description", OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        repository.addLink(link);
        OffsetDateTime newTime =
                OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC).plusDays(1);

        // when

        repository.setUpdateTime(1L, newTime);

        // then

        assertEquals(newTime, repository.getUpdateTime(1L));
        Link updatedLink = repository.getLinks().get(1L);
        assertEquals("https://test.ru", updatedLink.url());
        assertEquals("description", updatedLink.description());
    }

    @Test
    void checkGetLinkByIdExisting() {
        // given

        Link link = new Link(
                1L, "https://test.ru", "description", OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));
        repository.addLink(link);

        // when

        Link response = repository.getLinkById(1L);

        // then

        assertEquals(1L, response.id());
        assertEquals("https://test.ru", response.url());
        assertEquals("description", response.description());
    }

    @Test
    void checkGetLinkByIdNonExisting() {
        // given-when

        Link response = repository.getLinkById(999L);

        // then

        assertNull(response);
    }

    @Test
    void checkGetIdByUrlExisting() {
        // given

        Link link = new Link(
                1L, "https://test.ru", "description", OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC));
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
