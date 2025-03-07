package backend.academy.bot.controller;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class LinkUpdateController {
    private final BotService bot;

    @Autowired
    public LinkUpdateController(BotService bot) {
        this.bot = bot;
    }

    @PostMapping
    public ResponseEntity<String> processUpdate(@RequestBody LinkUpdate update) {
        update.tgChatIds().forEach(chatId ->
            bot.sendUpdate(chatId, "Update for " + update.url()));
        return ResponseEntity.ok("Обновление обработано");
    }
}
