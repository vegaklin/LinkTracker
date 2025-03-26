package backend.academy.scrapper.repository.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryChatLinksRepositoryTest {

    private InMemoryChatLinksRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryChatLinksRepository();
    }

    @Test
    void checkAddLinkNewChat() {
        // given-when

        repository.addLink(1L, 1L);

        // then

        Set<Long> links = repository.getLinksForChat(1L);

        assertEquals(1, links.size());
        assertTrue(links.contains(1L));
    }

    @Test
    void checkAddLinkExistingChat() {
        // given

        repository.addLink(1L, 1L);

        // when

        repository.addLink(1L, 2L);

        // then

        Set<Long> links = repository.getLinksForChat(1L);
        assertEquals(2, links.size());
        assertTrue(links.contains(1L));
        assertTrue(links.contains(2L));
    }

    @Test
    void checkGetLinksForChatExistingChat() {
        // given

        repository.addLink(1L, 1L);
        repository.addLink(1L, 2L);

        // when

        Set<Long> links = repository.getLinksForChat(1L);

        // then

        assertEquals(2, links.size());
        assertTrue(links.contains(1L));
        assertTrue(links.contains(2L));
    }

    @Test
    void checkGetLinksForChatNonExistingChat() {
        // given-when

        Set<Long> links = repository.getLinksForChat(1L);

        // then

        assertTrue(links.isEmpty());
    }

    @Test
    void checkRemoveLinkSuccessful() {
        // given

        repository.addLink(1L, 1L);
        repository.addLink(1L, 2L);

        // when

        boolean removed = repository.removeLink(1L, 1L);

        // then

        assertTrue(removed);
        Set<Long> links = repository.getLinksForChat(1L);
        assertEquals(1, links.size());
        assertFalse(links.contains(1L));
        assertTrue(links.contains(2L));
    }

    @Test
    void checkRemoveLinkNonExistingLink() {
        // given

        repository.addLink(1L, 1L);

        // when

        boolean removed = repository.removeLink(1L, 2L);

        // then

        assertFalse(removed);
        Set<Long> links = repository.getLinksForChat(1L);
        assertEquals(1, links.size());
        assertTrue(links.contains(1L));
    }

    @Test
    void checkRemoveLinkNonExistingChat() {
        // given-when

        boolean removed = repository.removeLink(1L, 1L);

        // then

        assertFalse(removed);
        assertTrue(repository.getLinksForChat(1L).isEmpty());
    }

    @Test
    void checkRemoveChatLinks() {
        // given

        repository.addLink(1L, 1L);
        repository.addLink(1L, 2L);

        // when

        repository.removeChatLinks(1L);

        // then

        assertTrue(repository.getLinksForChat(1L).isEmpty());
    }
}
