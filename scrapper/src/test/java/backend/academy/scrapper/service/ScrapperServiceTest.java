package backend.academy.scrapper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.repository.ChatLinksRepository;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.model.ChatLink;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScrapperServiceTest {

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
    void checkDeleteChatSuccess() {
        // given

        Mockito.when(chatRepository.deleteChat(1L)).thenReturn(true);

        // when

        scrapperService.deleteChat(1L);

        // then

        Mockito.verify(chatRepository).deleteChat(1L);
    }

    @Test
    void checkDeleteChatNotFound() {
        // given-when

        Mockito.when(chatRepository.deleteChat(1L)).thenReturn(false);

        ChatNotFoundException exception =
                assertThrows(ChatNotFoundException.class, () -> scrapperService.deleteChat(1L));

        // then

        assertEquals("Чат не найден с id: 1", exception.getMessage());
        Mockito.verify(chatRepository).deleteChat(1L);
    }

    @Test
    void checkAddLinkSuccess() {
        // given

        Long chatId = 1L;
        Long chatRowId = 10L;
        Long linkId = 20L;
        String url = "https://test.com";
        List<String> tags = List.of("tag1");
        List<String> filters = List.of("filter1");

        AddLinkRequest request = new AddLinkRequest(url, tags, filters);
        ChatLink chatLink = new ChatLink(chatRowId, linkId, tags, filters);

        Mockito.when(chatRepository.findIdByChatId(chatId)).thenReturn(chatRowId);
        Mockito.when(linkRepository.addLink(url)).thenReturn(linkId);
        Mockito.when(chatLinksRepository.getChatLinkByChatIdAndLinkId(chatRowId, linkId))
                .thenReturn(chatLink);

        // when

        LinkResponse response = scrapperService.addLink(chatId, request);

        // then

        assertEquals(linkId, response.id());
        assertEquals(url, response.url());
        assertEquals(tags, response.tags());
        assertEquals(filters, response.filters());

        Mockito.verify(chatRepository).findIdByChatId(chatId);
        Mockito.verify(linkRepository).addLink(url);
        Mockito.verify(chatLinksRepository).addLink(Mockito.any());
    }

    @Test
    void checkAddLinkChatNotFound() {
        // given-when

        Mockito.when(chatRepository.findIdByChatId(1L)).thenReturn(null);

        AddLinkRequest request = new AddLinkRequest("https://test.com", List.of(), List.of());

        ChatNotFoundException exception =
                assertThrows(ChatNotFoundException.class, () -> scrapperService.addLink(1L, request));

        // then

        assertEquals("Id не найдена для chatId: 1", exception.getMessage());
    }

    @Test
    void checkRemoveLinkSuccess() {
        // given

        Long chatId = 1L;
        Long chatRowId = 10L;
        Long linkId = 20L;
        String url = "https://test.com";
        List<String> tags = List.of("tag1");
        List<String> filters = List.of("filter1");

        ChatLink chatLink = new ChatLink(chatRowId, linkId, tags, filters);
        RemoveLinkRequest request = new RemoveLinkRequest(url);

        Mockito.when(linkRepository.getIdByUrl(url)).thenReturn(linkId);
        Mockito.when(chatRepository.findIdByChatId(chatId)).thenReturn(chatRowId);
        Mockito.when(chatLinksRepository.getChatLinkByChatIdAndLinkId(chatRowId, linkId))
                .thenReturn(chatLink);
        Mockito.when(chatLinksRepository.removeLink(chatRowId, linkId)).thenReturn(true);

        // when

        LinkResponse response = scrapperService.removeLink(chatId, request);

        // then

        assertEquals(linkId, response.id());
        assertEquals(url, response.url());
        assertEquals(tags, response.tags());
        assertEquals(tags, response.filters());

        Mockito.verify(chatLinksRepository).removeLink(chatRowId, linkId);
    }

    @Test
    void checkRemoveLinkNotFoundByUrl() {
        // given-when

        Mockito.when(linkRepository.getIdByUrl("https://test.com")).thenReturn(null);

        RemoveLinkRequest request = new RemoveLinkRequest("https://test.com");

        LinkNotFoundException exception =
                assertThrows(LinkNotFoundException.class, () -> scrapperService.removeLink(1L, request));

        // then

        assertEquals("Ссылка не найдена для URL: https://test.com", exception.getMessage());
    }

    @Test
    void checkRemoveLinkChatNotFound() {
        // given-when

        Mockito.when(linkRepository.getIdByUrl("https://test.com")).thenReturn(20L);
        Mockito.when(chatRepository.findIdByChatId(1L)).thenReturn(null);

        RemoveLinkRequest request = new RemoveLinkRequest("https://test.com");

        ChatNotFoundException exception =
                assertThrows(ChatNotFoundException.class, () -> scrapperService.removeLink(1L, request));

        // then

        assertEquals("Id не найдена для chatId: 1", exception.getMessage());
    }

    @Test
    void checkRemoveLinkNotFoundInChat() {
        // given-when

        Long chatRowId = 10L;
        Long linkId = 20L;

        Mockito.when(linkRepository.getIdByUrl("https://test.com")).thenReturn(linkId);
        Mockito.when(chatRepository.findIdByChatId(1L)).thenReturn(chatRowId);
        Mockito.when(chatLinksRepository.removeLink(chatRowId, linkId)).thenReturn(false);
        Mockito.when(chatLinksRepository.getChatLinkByChatIdAndLinkId(chatRowId, linkId))
                .thenReturn(new ChatLink(chatRowId, linkId, List.of(), List.of()));

        RemoveLinkRequest request = new RemoveLinkRequest("https://test.com");

        LinkNotFoundException exception =
                assertThrows(LinkNotFoundException.class, () -> scrapperService.removeLink(1L, request));

        // then

        assertEquals("Ссылка с id 20 не найдена для чата с id 1", exception.getMessage());
    }

    @Test
    void checkGetAllLinksSuccess() {
        // given

        Long chatId = 1L;
        Long chatRowId = 10L;
        Long linkId = 20L;
        String url = "https://test.com";
        List<String> tags = List.of("tag1");
        List<String> filters = List.of("filter1");

        Mockito.when(chatRepository.findIdByChatId(chatId)).thenReturn(chatRowId);
        Mockito.when(chatLinksRepository.getLinksForChat(chatRowId)).thenReturn(List.of(linkId));
        Mockito.when(linkRepository.getLinkById(linkId)).thenReturn(url);
        Mockito.when(chatLinksRepository.getChatLinkByChatIdAndLinkId(chatRowId, linkId))
                .thenReturn(new ChatLink(chatRowId, linkId, tags, filters));

        // when

        ListLinksResponse response = scrapperService.getAllLinks(chatId);

        // then

        assertEquals(1, response.size());
        assertEquals(url, response.links().getFirst().url());
        assertEquals(tags, response.links().getFirst().tags());
        assertEquals(filters, response.links().getFirst().filters());
    }

    @Test
    void checkGetAllLinksChatNotFound() {
        // given-when

        Mockito.when(chatRepository.findIdByChatId(1L)).thenReturn(null);

        ChatNotFoundException exception =
                assertThrows(ChatNotFoundException.class, () -> scrapperService.getAllLinks(1L));

        // then

        assertEquals("Id не найдена для chatId: 1", exception.getMessage());
    }
}
