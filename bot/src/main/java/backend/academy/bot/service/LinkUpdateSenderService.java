package backend.academy.bot.service;

import backend.academy.bot.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateSenderService {

    private final TelegramMessenger telegramMessenger;

    public void sendLinkUpdate(LinkUpdate linkUpdate) {
        linkUpdate.tgChatIds().forEach(chatId ->
            telegramMessenger.sendMessage(chatId, "Новое обновление по ссылке: " + linkUpdate.url())
        );
    }
}
