package backend.academy.bot.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.LinkUpdateSenderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LinkUpdateControllerTest {

    @Mock
    private LinkUpdateSenderService linkUpdateSenderService;

    @InjectMocks
    private LinkUpdateController linkUpdateController;

    @Test
    void checkSendLinkUpdateValidRequestReturnOk() {
        // given

        LinkUpdate linkUpdate = new LinkUpdate(1L, "http://test.ru", "description", List.of(1L));

        Mockito.doNothing().when(linkUpdateSenderService).sendLinkUpdate(linkUpdate);

        // when

        ResponseEntity<String> response = linkUpdateController.handleLinkUpdate(linkUpdate);

        // then

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Обновление обработано", response.getBody());

        Mockito.verify(linkUpdateSenderService).sendLinkUpdate(linkUpdate);
    }
}
