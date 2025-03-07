package backend.academy.bot.service;

public record BotState(long chatId, State state, String currentLink) {}
