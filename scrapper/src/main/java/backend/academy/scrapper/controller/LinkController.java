package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.ScrapperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinkController {

    private final ScrapperService scrapperService;

    @GetMapping
    public ResponseEntity<ListLinksResponse> handleGetAllLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId) {
        log.info("Received request to get all links for chatId: {}", tgChatId);
        ListLinksResponse response = scrapperService.getAllLinks(tgChatId);
        log.info("Returning {} links for chatId: {}", response.links().size(), tgChatId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LinkResponse> handleAddLink(
            @RequestHeader("Tg-Chat-Id") Long tgChatId, @RequestBody @Valid AddLinkRequest request) {
        log.info("Received request to add link '{}' for chatId: {}", request.link(), tgChatId);
        LinkResponse response = scrapperService.addLink(tgChatId, request);
        log.info("Successfully added link '{}' for chatId: {}", response.url(), tgChatId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<LinkResponse> handleRemoveLink(
            @RequestHeader("Tg-Chat-Id") Long tgChatId, @RequestBody @Valid RemoveLinkRequest request) {
        log.info("Received request to remove link '{}' for chatId: {}", request.link(), tgChatId);
        LinkResponse response = scrapperService.removeLink(tgChatId, request);
        log.info("Successfully removed link '{}' for chatId: {}", response.url(), tgChatId);
        return ResponseEntity.ok(response);
    }
}
