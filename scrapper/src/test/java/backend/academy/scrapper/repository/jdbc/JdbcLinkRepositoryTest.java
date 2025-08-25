package backend.academy.scrapper.repository.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = "app.access-type=SQL")
class JdbcLinkRepositoryTest {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        jdbcClient
                .sql("""
                TRUNCATE chats, links, chat_links
                RESTART IDENTITY CASCADE;""")
                .update();
    }

    @Test
    void testAddLink() {
        String url = "https://example.com";
        Long id = linkRepository.addLink(url);

        assertNotNull(id);
        String retrievedUrl = linkRepository.getLinkById(id);
        assertEquals(url, retrievedUrl);
    }

    @Test
    void testAddDuplicateLink() {
        String url = "https://example.com";
        Long id1 = linkRepository.addLink(url);
        Long id2 = linkRepository.addLink(url);

        assertEquals(id1, id2);
    }

    @Test
    void testGetLinks() {
        linkRepository.addLink("https://example1.com");
        linkRepository.addLink("https://example2.com");

        List<Link> links = linkRepository.getLinks(10, 0);
        assertEquals(2, links.size());
        assertEquals("https://example1.com", links.get(0).url());
        assertEquals("https://example2.com", links.get(1).url());
    }

    @Test
    void testCountLinks() {
        linkRepository.addLink("https://example1.com");
        linkRepository.addLink("https://example2.com");

        Long count = linkRepository.countLinks();
        assertEquals(2L, count);
    }

    @Test
    void testGetUpdateTime() {
        Long id = linkRepository.addLink("https://example.com");
        OffsetDateTime updateTime = linkRepository.getUpdateTime(id);

        assertNotNull(updateTime);
    }

    @Test
    void testSetUpdateTime() {
        Long id = linkRepository.addLink("https://example.com");
        OffsetDateTime newTime = OffsetDateTime.now();
        linkRepository.setUpdateTime(id, newTime);

        OffsetDateTime retrievedTime = linkRepository.getUpdateTime(id);
        assertEquals(newTime.toInstant(), retrievedTime.toInstant());
    }

    @Test
    void testSetDescription() {
        Long id = linkRepository.addLink("https://example.com");
        String description = "Test description";
        linkRepository.setDescription(id, description);

        String retrievedUrl = linkRepository.getLinkById(id);
        // Проверяем, что URL остался тем же
        assertEquals("https://example.com", retrievedUrl);
        // Для проверки description нужно добавить метод в LinkRepository или изменить модель
    }

    @Test
    void testGetIdByUrl() {
        String url = "https://example.com";
        Long id = linkRepository.addLink(url);
        Long retrievedId = linkRepository.getIdByUrl(url);

        assertEquals(id, retrievedId);
    }

    @Test
    void testGetLinkByIdNotFound() {
        String url = linkRepository.getLinkById(999L);
        assertNull(url);
    }

    @Test
    void testGetIdByUrlNotFound() {
        Long id = linkRepository.getIdByUrl("https://nonexistent.com");
        assertNull(id);
    }
}
