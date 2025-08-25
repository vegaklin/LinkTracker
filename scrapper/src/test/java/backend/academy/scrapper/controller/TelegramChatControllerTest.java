package backend.academy.scrapper.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.service.ScrapperService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TelegramChatController.class)
class TelegramChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScrapperService scrapperService;

    @Test
    @SneakyThrows
    void checkRegisterChatReturnOk() {
        // given

        Long chatId = 1L;

        // when-then

        mockMvc.perform(post("/tg-chat/{id}", chatId)).andExpect(status().isOk());

        verify(scrapperService, times(1)).registerChat(chatId);
    }

    @Test
    @SneakyThrows
    void checkDeleteChatReturnOk() {
        // given

        Long chatId = 1L;

        // when-then

        mockMvc.perform(delete("/tg-chat/{id}", chatId)).andExpect(status().isOk());

        verify(scrapperService, times(1)).deleteChat(chatId);
    }

    @Test
    @SneakyThrows
    void checkDeleteChatReturnNotFound() {
        // given

        Long chatId = 1L;
        doThrow(new ChatNotFoundException("Чат не найден с id: " + chatId))
                .when(scrapperService)
                .deleteChat(chatId);

        // when-then

        mockMvc.perform(delete("/tg-chat/{id}", chatId)).andExpect(status().isNotFound());
    }
}
