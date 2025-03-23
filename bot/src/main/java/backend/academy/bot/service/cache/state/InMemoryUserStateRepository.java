package backend.academy.bot.service.cache.state;

import backend.academy.bot.service.model.BotState;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserStateRepository implements UserStateRepository {
    private final Map<Long, BotState> userStates = new HashMap<>();

    @Override
    public BotState getState(long chatId) {
        return userStates.getOrDefault(chatId, BotState.DEFAULT);
    }

    @Override
    public void setState(long chatId, BotState state) {
        userStates.put(chatId, state);
    }
}
