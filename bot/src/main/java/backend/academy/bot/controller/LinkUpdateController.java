package backend.academy.bot.controller;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.LinkUpdateSenderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class LinkUpdateController {

    private final LinkUpdateSenderService linkUpdateSenderService;

    @PostMapping
    public ResponseEntity<String> handleLinkUpdate(@RequestBody @Valid LinkUpdate linkUpdate) {
        log.info("Received link update: {}", linkUpdate.url());
        linkUpdateSenderService.sendLinkUpdate(linkUpdate);
        log.info("Link update processed successfully");
        return ResponseEntity.ok("Обновление обработано");
    }
}
