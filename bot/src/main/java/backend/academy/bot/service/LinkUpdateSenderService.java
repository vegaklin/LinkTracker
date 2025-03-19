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
        log.info("Sending link update for URL: {} to {} chat(s)", linkUpdate.url(), linkUpdate.tgChatIds().size());
        linkUpdate.tgChatIds().forEach(chatId -> {
            log.info("Sending update to chatId={}", chatId);
            telegramMessenger.sendMessage(chatId, "Новое обновление по ссылке: " + linkUpdate.url());
        });
    }
}
