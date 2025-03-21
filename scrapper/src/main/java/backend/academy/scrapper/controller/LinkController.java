package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.ScrapperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinkController {

    private final ScrapperService scrapperService;

    @GetMapping
    public ResponseEntity<ListLinksResponse> handleGetAllLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId) {
        ListLinksResponse response = scrapperService.getAllLinks(tgChatId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LinkResponse> handleAddLink(
            @RequestHeader("Tg-Chat-Id") Long tgChatId,
            @RequestBody @Valid AddLinkRequest request
    ) {
        LinkResponse response = scrapperService.addLink(tgChatId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<LinkResponse> handleRemoveLink(
            @RequestHeader("Tg-Chat-Id") Long tgChatId,
            @RequestBody @Valid RemoveLinkRequest request
    ) {
        LinkResponse response = scrapperService.removeLink(tgChatId, request);
        return ResponseEntity.ok(response);
    }
}
