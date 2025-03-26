package backend.academy.bot.service.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.client.dto.LinkResponse;
import backend.academy.bot.client.dto.ListLinksResponse;
import backend.academy.bot.dto.ApiErrorResponse;
import backend.academy.bot.exception.ScrapperClientException;
import backend.academy.bot.service.TelegramMessenger;
import backend.academy.bot.service.util.LinkUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest {

    @Mock
    private TelegramMessenger telegramMessenger;

    @Mock
    private ScrapperClient scrapperClient;

    @InjectMocks
    private ListCommand listCommand;

    @Test
    void checkCommandName() {
        // given-when

        String commandName = listCommand.commandName();

        // then

        assertEquals("/list", commandName);
    }

    @Test
    void checkHandleNonEmptyLinks() {
        // given

        List<LinkResponse> links = List.of(
                new LinkResponse(1L, "https://test1.ru", List.of("tag1", "tag2"), List.of("filter:filter1", "filter:filter3")),
                new LinkResponse(2L, "https://test2.ru", List.of("tag2"), List.of("filter:filter2")));
        Mockito.when(scrapperClient.getAllLinks(1L)).thenReturn(Mono.just(new ListLinksResponse(links, links.size())));
        String expectedMessage = links.stream()
                .map(LinkUtils::formatLink)
                .collect(Collectors.joining("\n\n", "Отслеживаемые ссылки:\n", "\n"));

        // when

        listCommand.handle(1L, "message");

        // then

        Mockito.verify(scrapperClient).getAllLinks(1L);
        Mockito.verify(telegramMessenger).sendMessage(1L, expectedMessage);
    }

    @Test
    void checkHandleEmptyLinks() {
        // given

        Mockito.when(scrapperClient.getAllLinks(1L))
                .thenReturn(Mono.just(new ListLinksResponse(Collections.emptyList(), 0)));

        // when

        listCommand.handle(1L, "message");

        // then

        Mockito.verify(scrapperClient).getAllLinks(1L);
        Mockito.verify(telegramMessenger).sendMessage(1L, "Список отслеживаемых ссылок пуст");
    }

    @Test
    void checkHandleScrapperClientException() {
        // given

        Mockito.when(scrapperClient.getAllLinks(1L))
                .thenThrow(new ScrapperClientException(
                        new ApiErrorResponse("API error", "temp", "temp", "temp", List.of())));

        // when

        listCommand.handle(1L, "message");

        // then

        Mockito.verify(scrapperClient).getAllLinks(1L);
        Mockito.verify(telegramMessenger).sendMessage(1L, "Ошибка при получении списка: API error");
    }
}
