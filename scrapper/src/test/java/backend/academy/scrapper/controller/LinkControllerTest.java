package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.ScrapperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LinkControllerTest {

    @Mock
    private ScrapperService scrapperService;

    @InjectMocks
    private LinkController linkController;

    @Test
    void checkHandleGetAllLinksValidRequestReturnOk() {
        // given

        ListLinksResponse listLinksResponse = new ListLinksResponse(List.of(
            new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"))),
            1
        );

        Mockito.when(scrapperService.getAllLinks(1L)).thenReturn(listLinksResponse);

        // when

        ResponseEntity<ListLinksResponse> response = linkController.handleGetAllLinks(1L);

        // then

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listLinksResponse, response.getBody());

        Mockito.verify(scrapperService).getAllLinks(1L);
    }

    @Test
    void checkHandleAddLinkValidRequestReturnOk() {
        // given

        AddLinkRequest addLinkRequest = new AddLinkRequest("https://test.ru", List.of("tag1"), List.of("filter:filter1"));
        LinkResponse linkResponse = new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"));

        Mockito.when(scrapperService.addLink(1L, addLinkRequest)).thenReturn(linkResponse);

        // when

        ResponseEntity<LinkResponse> response = linkController.handleAddLink(1L, addLinkRequest);

        // then

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(linkResponse, response.getBody());

        Mockito.verify(scrapperService).addLink(1L, addLinkRequest);
    }

    @Test
    void checkHandleRemoveLinkValidRequestReturnOk() {
        // given

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("https://test.ru");
        LinkResponse linkResponse = new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"));

        Mockito.when(scrapperService.removeLink(1L, removeLinkRequest)).thenReturn(linkResponse);

        // when

        ResponseEntity<LinkResponse> response = linkController.handleRemoveLink(1L, removeLinkRequest);

        // then

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(linkResponse, response.getBody());

        Mockito.verify(scrapperService).removeLink(1L, removeLinkRequest);
    }
}
