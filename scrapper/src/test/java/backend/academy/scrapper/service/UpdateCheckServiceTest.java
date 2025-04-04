package backend.academy.scrapper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.service.api.ApiProcess;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class UpdateCheckServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ApiProcess gitHubApiProcess;

    @Mock
    private ApiProcess stackOverflowApiProcess;

    @Mock
    private UpdateSenderService updateSenderService;

    @InjectMocks
    private UpdateCheckService updateCheckService;

    @BeforeEach
    void setUp() {
        updateCheckService = new UpdateCheckService(
                linkRepository, List.of(gitHubApiProcess, stackOverflowApiProcess), updateSenderService);
    }

    @Test
    void checkLinkUpdateCheckSuccessfulUpdate() {
        // given

        OffsetDateTime oldTime =
                OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC).minusDays(1);
        OffsetDateTime newTime = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        Mockito.when(gitHubApiProcess.isApiUrl("https://github.com/owner/repo")).thenReturn(true);
        Mockito.when(gitHubApiProcess.checkUpdate("https://github.com/owner/repo"))
                .thenReturn(Mono.just(newTime));
        Mockito.when(linkRepository.getUpdateTime(1L)).thenReturn(oldTime);

        // when

        updateCheckService.checkLinkUpdate(1L, "https://github.com/owner/repo");

        // then

        Mockito.verify(linkRepository).setUpdateTime(1L, newTime);
        Mockito.verify(updateSenderService).notifyChatsForLink(1L, "https://github.com/owner/repo");
        Mockito.verify(linkRepository).getUpdateTime(1L);
    }

    @Test
    void checkLinkUpdateCheckNoUpdateTime() {
        // given

        Mockito.when(gitHubApiProcess.isApiUrl("https://github.com/owner/repo")).thenReturn(true);
        Mockito.when(gitHubApiProcess.checkUpdate("https://github.com/owner/repo"))
                .thenReturn(Mono.empty());

        // when

        updateCheckService.checkLinkUpdate(1L, "https://github.com/owner/repo");

        // then

        Mockito.verify(linkRepository, Mockito.never()).setUpdateTime(Mockito.anyLong(), Mockito.any());
        Mockito.verify(updateSenderService, Mockito.never()).notifyChatsForLink(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(linkRepository, Mockito.never()).getUpdateTime(Mockito.anyLong());
    }

    @Test
    void checkLinkUpdateCheckNoUpdate() {
        // given

        OffsetDateTime currentTime = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        Mockito.when(gitHubApiProcess.isApiUrl("https://github.com/owner/repo")).thenReturn(true);
        Mockito.when(gitHubApiProcess.checkUpdate("https://github.com/owner/repo"))
                .thenReturn(Mono.just(currentTime));
        Mockito.when(linkRepository.getUpdateTime(1L)).thenReturn(currentTime);

        // when

        updateCheckService.checkLinkUpdate(1L, "https://github.com/owner/repo");

        // then

        Mockito.verify(linkRepository, Mockito.never()).setUpdateTime(Mockito.anyLong(), Mockito.any());
        Mockito.verify(updateSenderService, Mockito.never()).notifyChatsForLink(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(linkRepository).getUpdateTime(1L);
    }

    @Test
    void checkUpdateCheckValidApiProcess() {
        // given

        OffsetDateTime updateTime = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        Mockito.when(gitHubApiProcess.isApiUrl("https://stackoverflow.com/questions"))
                .thenReturn(false);
        Mockito.when(stackOverflowApiProcess.isApiUrl("https://stackoverflow.com/questions"))
                .thenReturn(true);
        Mockito.when(stackOverflowApiProcess.checkUpdate("https://stackoverflow.com/questions"))
                .thenReturn(Mono.just(updateTime));

        // when

        OffsetDateTime result = updateCheckService
                .checkUpdate("https://stackoverflow.com/questions")
                .block();

        // then
        assertNotNull(result);
        assertEquals(updateTime, result);
        Mockito.verify(gitHubApiProcess, Mockito.never()).checkUpdate("https://stackoverflow.com/questions");
        Mockito.verify(stackOverflowApiProcess).checkUpdate("https://stackoverflow.com/questions");
    }

    @Test
    void checkUpdateCheckNoMatchingApiProcess() {
        // given

        Mockito.when(gitHubApiProcess.isApiUrl("https://stackoverflow.com/questions"))
                .thenReturn(false);
        Mockito.when(stackOverflowApiProcess.isApiUrl("https://stackoverflow.com/questions"))
                .thenReturn(false);

        // when

        OffsetDateTime result = updateCheckService
                .checkUpdate("https://stackoverflow.com/questions")
                .block();

        // then

        assertNull(result);
        Mockito.verify(gitHubApiProcess, Mockito.never()).checkUpdate("https://stackoverflow.com/questions");
        Mockito.verify(stackOverflowApiProcess, Mockito.never()).checkUpdate("https://stackoverflow.com/questions");
    }
}
