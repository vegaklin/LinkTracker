package backend.academy.bot.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.LinkUpdateSenderService;
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

@WebMvcTest(LinkUpdateController.class)
class LinkUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkUpdateSenderService linkUpdateSenderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void checkLinkUpdateReturnOkWhenValidRequest() {
        // given

        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://test.ru", "Test update", List.of(1L, 2L));
        String jsonRequest = objectMapper.writeValueAsString(linkUpdate);

        // when-then

        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление обработано"));

        Mockito.verify(linkUpdateSenderService, Mockito.times(1)).sendLinkUpdate(linkUpdate);
    }

    @Test
    @SneakyThrows
    void checkLinkUpdateReturnBadRequestWhenInvalidRequest() {
        // given

        LinkUpdate invalidLinkUpdate = new LinkUpdate(1L, null, "", List.of(1L, 2L));
        String jsonRequest = objectMapper.writeValueAsString(invalidLinkUpdate);

        // when-then

        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"description\":\"Некорректные параметры запроса\",\"code\":\"400\"}"));

        Mockito.verify(linkUpdateSenderService, Mockito.never()).sendLinkUpdate(Mockito.any());
    }
}
