package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.ScrapperService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ListLinksResponse handleGetAllLinks(@RequestHeader("Tg-Chat-Id") @NotNull Long tgChatId) {
        log.info("Received request to get all links for chatId: {}", tgChatId.toString());
        return scrapperService.getAllLinks(tgChatId);
    }

    @PostMapping
    public LinkResponse handleAddLink(
            @RequestHeader("Tg-Chat-Id") Long tgChatId, @RequestBody @Valid AddLinkRequest request) {
        log.info("Received request to add link '{}' for chatId: {}", request.link(), tgChatId.toString());
        return scrapperService.addLink(tgChatId, request);
    }

    @DeleteMapping
    public LinkResponse handleRemoveLink(
            @RequestHeader("Tg-Chat-Id") Long tgChatId, @RequestBody @Valid RemoveLinkRequest request) {
        log.info("Received request to remove link '{}' for chatId: {}", request.link(), tgChatId.toString());
        return scrapperService.removeLink(tgChatId, request);
    }
}
