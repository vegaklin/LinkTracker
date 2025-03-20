package backend.academy.bot.service.repository.state;

import backend.academy.bot.service.model.BotState;

public interface UserStateRepository {
    BotState getState(long chatId);

    void setState(long chatId, BotState state);
}
