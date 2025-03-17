package backend.academy.bot.controller;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.BotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/updates")
public class LinkUpdateController {
    private final BotService botService;

    @Autowired
    public LinkUpdateController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping
    public ResponseEntity<String> handleLinkUpdate(@RequestBody @Valid LinkUpdate linkUpdate) {
        System.out.println("handleLinkUpdate");
        botService.sendLinkUpdate(linkUpdate);
        return ResponseEntity.ok("Обновление обработано");
    }
}
