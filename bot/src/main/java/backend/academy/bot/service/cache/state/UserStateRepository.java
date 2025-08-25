package backend.academy.bot.service.cache.state;

import backend.academy.bot.service.model.BotState;

public interface UserStateRepository {
    BotState getState(long chatId);

    void setState(long chatId, BotState state);
}
