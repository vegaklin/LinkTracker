package backend.academy.bot.service.cache.state;

import backend.academy.bot.service.model.BotState;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class InMemoryUserStateRepository implements UserStateRepository {
    private final Map<Long, BotState> userStates = new ConcurrentHashMap<>();

    @Override
    public BotState getState(long chatId) {
        BotState state = userStates.getOrDefault(chatId, BotState.DEFAULT);
        if (state != BotState.DEFAULT) {
            log.info("Fetched state for chatId: {} - State: {}", chatId, state);
        } else {
            log.info("No state found for chatId: {}. Returning default state.", chatId);
        }
        return state;
    }

    @Override
    public void setState(long chatId, BotState state) {
        userStates.put(chatId, state);
        log.info("Set state for chatId: {} - State: {}", chatId, state);
    }
}
