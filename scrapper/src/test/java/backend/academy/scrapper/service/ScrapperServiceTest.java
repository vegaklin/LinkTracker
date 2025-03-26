package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.repository.chat.ChatLinksRepository;
import backend.academy.scrapper.repository.chat.ChatRepository;
import backend.academy.scrapper.repository.link.LinkRepository;
import backend.academy.scrapper.repository.link.model.Link;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ScrapperServiceTest {

    @Mock
    private UpdateCheckService updateCheckService;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatLinksRepository chatLinksRepository;

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private ScrapperService scrapperService;

    @Test
    void checkRegisterChat() {
        // given-when

        scrapperService.registerChat(1L);

        // then

        Mockito.verify(chatRepository).registerChat(1L);
    }

    @Test
    void checkDeleteChat() {
        // given-when

        scrapperService.deleteChat(1L);

        // then

        Mockito.verify(chatLinksRepository).removeChatLinks(1L);
        Mockito.verify(chatRepository).deleteChat(1L);
    }

    @Test
    void checkAddLink() {
        // given

        OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        AddLinkRequest request = new AddLinkRequest("https://test.ru", List.of("tag1"), List.of("filter:filter1"));
        Link link = new Link("https://test.ru", List.of("tag1"), List.of("filter:filter1"), offsetDateTime);
        LinkResponse response = new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"));

        Mockito.when(updateCheckService.checkUpdate("https://test.ru")).thenReturn(Mono.just(offsetDateTime));
        Mockito.when(linkRepository.addLink(link)).thenReturn(response);

        // when

        LinkResponse result = scrapperService.addLink(1L, request);

        // then

        assertEquals(response, result);

        Mockito.verify(updateCheckService).checkUpdate("https://test.ru");
        Mockito.verify(linkRepository).addLink(link);
        Mockito.verify(chatLinksRepository).addLink(1L, 1L);
    }

    @Test
    void checkRemoveLinkSuccess() {
        // given

        RemoveLinkRequest request = new RemoveLinkRequest("https://test.ru");
        LinkResponse response = new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"));

        Mockito.when(linkRepository.getIdByUrl("https://test.ru")).thenReturn(1L);
        Mockito.when(chatLinksRepository.removeLink(1L, 1L)).thenReturn(true);
        Mockito.when(linkRepository.getLinkById(1L)).thenReturn(response);

        // when

        LinkResponse result = scrapperService.removeLink(1L, request);

        // then

        assertEquals(response, result);

        Mockito.verify(linkRepository).getIdByUrl("https://test.ru");
        Mockito.verify(chatLinksRepository).removeLink(1L, 1L);
        Mockito.verify(linkRepository).getLinkById(1L);
    }

    @Test
    void checkRemoveLinkAndLinkNotFoundByUrl() {
        // given

        RemoveLinkRequest request = new RemoveLinkRequest("https://test.ru");

        Mockito.when(linkRepository.getIdByUrl("https://test.ru")).thenReturn(null);

        // when-then

        LinkNotFoundException exception = assertThrows(LinkNotFoundException.class,
            () -> scrapperService.removeLink(1L, request));
        assertEquals("Ссылка не найдена для URL: https://test.ru", exception.getMessage());

        Mockito.verify(linkRepository).getIdByUrl("https://test.ru");
        Mockito.verifyNoMoreInteractions(chatLinksRepository, linkRepository);
    }

    @Test
    void checkRemoveLinkAndLinkNotFoundInChat() {
        // given

        RemoveLinkRequest request = new RemoveLinkRequest("https://test.ru");

        Mockito.when(linkRepository.getIdByUrl("https://test.ru")).thenReturn(1L);
        Mockito.when(chatLinksRepository.removeLink(1L, 1L)).thenReturn(false);

        // when-then

        LinkNotFoundException exception = assertThrows(LinkNotFoundException.class,
            () -> scrapperService.removeLink(1L, request));
        assertEquals("Ссылка с id " + 1L + " не найдена для чата с id " + 1L, exception.getMessage());

        Mockito.verify(linkRepository).getIdByUrl("https://test.ru");
        Mockito.verify(chatLinksRepository).removeLink(1L, 1L);
        Mockito.verifyNoMoreInteractions(linkRepository);
    }

    @Test
    void checkGetAllLinks() {
        // given
        Set<Long> linkIds = Set.of(1L);
        LinkResponse linkResponse = new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"));

        Mockito.when(chatLinksRepository.getLinksForChat(1L)).thenReturn(linkIds);
        Mockito.when(linkRepository.getLinkById(1L)).thenReturn(linkResponse);

        // when

        ListLinksResponse result = scrapperService.getAllLinks(1L);

        // then

        assertEquals(1, result.size());
        assertEquals(List.of(linkResponse), result.links());

        Mockito.verify(chatLinksRepository).getLinksForChat(1L);
        Mockito.verify(linkRepository).getLinkById(1L);
    }

    @Test
    void checkGetAllLinks_linkNotFound() {
        // given
        Set<Long> linkIds = Set.of(1L);

        Mockito.when(chatLinksRepository.getLinksForChat(1L)).thenReturn(linkIds);
        Mockito.when(linkRepository.getLinkById(1L)).thenReturn(null);

        // when-then

        LinkNotFoundException exception = assertThrows(LinkNotFoundException.class,
            () -> scrapperService.getAllLinks(1L));
        assertEquals("Ссылка с id " + 1L + " не найдена", exception.getMessage());

        Mockito.verify(chatLinksRepository).getLinksForChat(1L);
        Mockito.verify(linkRepository).getLinkById(1L);
    }
}
