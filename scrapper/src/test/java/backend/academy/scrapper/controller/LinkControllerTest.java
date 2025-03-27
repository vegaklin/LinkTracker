package backend.academy.scrapper.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.service.ScrapperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(LinkController.class)
class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScrapperService scrapperService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void checkGetAllLinksReturnLinks() {
        // given

        Long chatId = 1L;
        ListLinksResponse response = new ListLinksResponse(
                List.of(new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"))), 1);
        Mockito.when(scrapperService.getAllLinks(chatId)).thenReturn(response);

        // when-then

        mockMvc.perform(MockMvcRequestBuilders.get("/links").header("Tg-Chat-Id", String.valueOf(chatId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links[0].url").value("https://test.ru"));

        Mockito.verify(scrapperService, Mockito.times(1)).getAllLinks(chatId);
    }

    @Test
    @SneakyThrows
    void checkAddLinkReturnAddedLink() {
        // given

        Long chatId = 1L;
        AddLinkRequest request = new AddLinkRequest("https://test.ru", List.of("tag1"), List.of("filter:filter1"));
        LinkResponse response = new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"));
        Mockito.when(scrapperService.addLink(chatId, request)).thenReturn(response);

        // when-then

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://test.ru"));

        Mockito.verify(scrapperService, Mockito.times(1)).addLink(chatId, request);
    }

    @Test
    @SneakyThrows
    void checkRemoveLinkReturnRemovedLink() {
        // given

        Long chatId = 1L;
        RemoveLinkRequest request = new RemoveLinkRequest("https://test.ru");
        LinkResponse response = new LinkResponse(1L, "https://test.ru", List.of("tag1"), List.of("filter:filter1"));
        Mockito.when(scrapperService.removeLink(chatId, request)).thenReturn(response);

        // when-then

        mockMvc.perform(MockMvcRequestBuilders.delete("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://test.ru"));

        Mockito.verify(scrapperService, Mockito.times(1)).removeLink(chatId, request);
    }

    @Test
    @SneakyThrows
    void checkAddLinkInvalidRequestReturnBadRequest() {
        // given

        Long chatId = 1L;
        AddLinkRequest invalidRequest = new AddLinkRequest(null, List.of("tag1"), List.of("filter:filter1"));

        // when-then

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"));

        Mockito.verify(scrapperService, Mockito.never()).addLink(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void checkAddLinkInvalidRequestReturnNotFound() {
        // given

        Long chatId = 1L;
        RemoveLinkRequest request = new RemoveLinkRequest("https://test.ru");
        Mockito.when(scrapperService.removeLink(chatId, request))
                .thenThrow(new LinkNotFoundException("LinkNotFoundException"));
        ;

        // when-then

        mockMvc.perform(MockMvcRequestBuilders.delete("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description").value("Ссылка не найдена"));
    }
}
