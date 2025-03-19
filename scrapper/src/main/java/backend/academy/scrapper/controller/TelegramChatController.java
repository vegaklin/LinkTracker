package backend.academy.scrapper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.academy.scrapper.service.ScrapperService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tg-chat")
@RequiredArgsConstructor
public class TelegramChatController {

    private final ScrapperService scrapperService;

    @PostMapping("/{id}")
    public ResponseEntity<Void> registerChat(@PathVariable Long id) {
        scrapperService.registerChat(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        scrapperService.deleteChat(id);
        return ResponseEntity.ok().build();
    }
}
