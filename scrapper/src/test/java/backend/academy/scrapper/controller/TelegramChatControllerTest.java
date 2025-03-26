package backend.academy.scrapper.controller;

import backend.academy.scrapper.service.ScrapperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TelegramChatControllerTest {

    @Mock
    private ScrapperService scrapperService;

    @InjectMocks
    private TelegramChatController telegramChatController;

    @Test
    void checkHandleRegisterChatValidRequestReturnOk() {
        // given

        Mockito.doNothing().when(scrapperService).registerChat(1L);

        // when

        ResponseEntity<String> response = telegramChatController.handleRegisterChat(1L);

        // then

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Чат зарегистрирован", response.getBody());

        Mockito.verify(scrapperService).registerChat(1L);
    }

    @Test
    void checkHandleDeleteChatValidRequestReturnOk() {
        // given

        Mockito.doNothing().when(scrapperService).deleteChat(1L);

        // when

        ResponseEntity<String> response = telegramChatController.handleDeleteChat(1L);

        // then

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Чат успешно удалён", response.getBody());

        Mockito.verify(scrapperService).deleteChat(1L);
    }
}
