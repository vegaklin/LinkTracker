package backend.academy.bot.repository;

import backend.academy.bot.service.BotState;
import backend.academy.bot.service.State;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository {
    private final Map<Long, BotState> states = new HashMap<>();

    public void registerUser(long chatId) {
        states.put(chatId, new BotState(chatId, State.IDLE, null));
    }

    public State getState(long chatId) {
        return states.getOrDefault(chatId, new BotState(chatId, State.IDLE, null)).state();
    }

    public void setState(long chatId, State state) {
        BotState current = states.getOrDefault(chatId, new BotState(chatId, State.IDLE, null));
        states.put(chatId, new BotState(chatId, state, current.currentLink()));
    }

    public String getCurrentLink(long chatId) {
        return states.getOrDefault(chatId, new BotState(chatId, State.IDLE, null)).currentLink();
    }

    public void setCurrentLink(long chatId, String link) {
        BotState current = states.getOrDefault(chatId, new BotState(chatId, State.IDLE, null));
        states.put(chatId, new BotState(chatId, current.state(), link));
    }
}
