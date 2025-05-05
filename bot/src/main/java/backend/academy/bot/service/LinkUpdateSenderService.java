package backend.academy.bot.service;

import backend.academy.bot.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkUpdateSenderService {

    private final TelegramMessenger telegramMessenger;

    public void sendLinkUpdate(LinkUpdate linkUpdate) {
        linkUpdate.tgChatIds().forEach(chatId -> {
            log.info("Sending update to chatId={}", chatId);
            String text = String.format("""
                Новое обновление по ссылке: %s
                %s
                """,
                linkUpdate.url(), linkUpdate.description()
            );
            telegramMessenger.sendMessage(chatId, text);
        });
    }
}
