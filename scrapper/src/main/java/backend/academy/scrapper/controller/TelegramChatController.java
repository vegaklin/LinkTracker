package backend.academy.scrapper.controller;

import backend.academy.scrapper.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
@RequiredArgsConstructor
public class TelegramChatController {

    private final ScrapperService scrapperService;

    @PostMapping("/{id}")
    public ResponseEntity<String> handleRegisterChat(@PathVariable Long id) {
        scrapperService.registerChat(id);
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> handleDeleteChat(@PathVariable Long id) {
        scrapperService.deleteChat(id);
        return ResponseEntity.ok("Чат успешно удалён");
    }
}
