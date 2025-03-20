package backend.academy.bot.service.command;

public interface CommandHandler {
    String commandName();
    void handle(long chatId, String message);
}
