package backend.academy.scrapper.repository.inmemory.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.repository.inmemory.InMemoryChatRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryChatRepositoryTest {

    private InMemoryChatRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryChatRepository();
    }

    @Test
    void checkRegisterChatNewChat() {
        // given-when

        repository.registerChat(1L);

        // then

        Set<Long> chatIds = repository.getChatIds();
        assertEquals(1, chatIds.size());
        assertTrue(chatIds.contains(1L));
    }

    @Test
    void checkRegisterChatExistingChat() {
        // given

        repository.registerChat(1L);

        // when

        repository.registerChat(1L);

        // then

        Set<Long> chatIds = repository.getChatIds();
        assertEquals(1, chatIds.size());
        assertTrue(chatIds.contains(1L));
    }

    @Test
    void checkDeleteChatExistingChat() {
        // given

        repository.registerChat(1L);
        repository.registerChat(2L);

        // when

        repository.deleteChat(1L);

        // then

        Set<Long> chatIds = repository.getChatIds();
        assertEquals(1, chatIds.size());
        assertFalse(chatIds.contains(1L));
        assertTrue(chatIds.contains(2L));
    }

    @Test
    void checkDeleteChatNonExistingChat() {
        // given

        repository.registerChat(1L);

        // when

        repository.deleteChat(2L);

        // then

        Set<Long> chatIds = repository.getChatIds();
        assertEquals(1, chatIds.size());
        assertTrue(chatIds.contains(1L));
    }

    @Test
    void checkGetChatIdsEmpty() {
        // given-when

        Set<Long> chatIds = repository.getChatIds();

        // then

        assertTrue(chatIds.isEmpty());
    }

    @Test
    void checkGetChatIdsNonEmpty() {
        // given

        repository.registerChat(1L);
        repository.registerChat(2L);

        // when

        Set<Long> chatIds = repository.getChatIds();

        // then

        assertEquals(2, chatIds.size());
        assertTrue(chatIds.contains(1L));
        assertTrue(chatIds.contains(2L));
    }
}
