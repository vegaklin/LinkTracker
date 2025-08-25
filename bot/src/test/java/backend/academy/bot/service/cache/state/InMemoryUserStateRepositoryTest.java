package backend.academy.bot.service.cache.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.bot.service.model.BotState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryUserStateRepositoryTest {

    private UserStateRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserStateRepository();
    }

    @Test
    void checkGetStateChatIdNotExistsReturnDefaultState() {
        // given

        long chatId = 1L;

        // when

        BotState state = repository.getState(chatId);

        // then

        assertEquals(BotState.DEFAULT, state);
    }

    @Test
    void checkSetStateValidChatIdAndState() {
        // given

        long chatId = 1L;
        BotState state = BotState.AWAITING_LINK;

        // when

        repository.setState(chatId, state);

        // then

        assertEquals(state, repository.getState(chatId));
    }

    @Test
    void checkSetStateOverrideState() {
        // given

        long chatId = 1L;
        repository.setState(chatId, BotState.DEFAULT);
        BotState newState = BotState.AWAITING_LINK;

        // when

        repository.setState(chatId, newState);

        // then

        assertEquals(newState, repository.getState(chatId));
    }
}
