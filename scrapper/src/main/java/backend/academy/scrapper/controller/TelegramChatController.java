package backend.academy.scrapper.controller;

import backend.academy.scrapper.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tg-chat")
@RequiredArgsConstructor
public class TelegramChatController {

    private final ScrapperService scrapperService;

    @PostMapping("/{id}")
    public ResponseEntity<String> handleRegisterChat(@PathVariable Long id) {
        log.info("Received request to register chat with id: {}", id.toString());
        scrapperService.registerChat(id);
        log.info("Chat with id {} registered successfully", id.toString());
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> handleDeleteChat(@PathVariable Long id) {
        log.info("Received request to delete chat with id: {}", id.toString());
        scrapperService.deleteChat(id);
        log.info("Chat with id {} deleted successfully", id.toString());
        return ResponseEntity.ok("Чат успешно удалён");
    }
}
