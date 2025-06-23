package backend.academy.scrapper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import backend.academy.scrapper.client.dto.ApiAnswer;
import backend.academy.scrapper.repository.LinkRepository;
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

@ExtendWith(MockitoExtension.class)
class UpdateCheckServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ApiProcess gitHubApiProcess;

    @Mock
    private ApiProcess stackOverflowApiProcess;

    @Mock
    private UpdateNotifyService updateNotifyService;

    @InjectMocks
    private UpdateCheckService updateCheckService;

    @BeforeEach
    void setUp() {
        updateCheckService = new UpdateCheckService(
                linkRepository, List.of(gitHubApiProcess, stackOverflowApiProcess), updateNotifyService);
    }

    @Test
    void checkLinkUpdateCheckSuccessfulUpdate() {
        // given

        OffsetDateTime oldTime =
                OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC).minusDays(1);
        OffsetDateTime newTime = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        ApiAnswer answer = new ApiAnswer("new description", newTime);

        Mockito.when(gitHubApiProcess.isApiUrl("https://github.com/owner/repo")).thenReturn(true);
        Mockito.when(gitHubApiProcess.checkUpdate("https://github.com/owner/repo"))
                .thenReturn(answer);
        Mockito.when(linkRepository.getUpdateTime(1L)).thenReturn(oldTime);

        // when

        updateCheckService.checkLinkUpdate(1L, "https://github.com/owner/repo");

        // then

        Mockito.verify(linkRepository).setUpdateTime(1L, newTime);
        Mockito.verify(linkRepository).setDescription(1L, "new description");
        Mockito.verify(updateNotifyService).notifyChatsForLink(1L, "https://github.com/owner/repo", "new description");
    }

    @Test
    void checkLinkUpdateCheckNoUpdateTime() {
        // given

        Mockito.when(gitHubApiProcess.isApiUrl("https://github.com/owner/repo")).thenReturn(true);
        Mockito.when(gitHubApiProcess.checkUpdate("https://github.com/owner/repo"))
                .thenReturn(null);

        // when

        updateCheckService.checkLinkUpdate(1L, "https://github.com/owner/repo");

        // then

        Mockito.verify(linkRepository, Mockito.never()).setUpdateTime(Mockito.anyLong(), Mockito.any());
        Mockito.verify(linkRepository, Mockito.never()).getUpdateTime(Mockito.anyLong());
        Mockito.verify(updateNotifyService, Mockito.never())
                .notifyChatsForLink(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void checkLinkUpdateCheckNoUpdate() {
        // given

        OffsetDateTime sameTime = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        ApiAnswer answer = new ApiAnswer("description", sameTime);

        Mockito.when(gitHubApiProcess.isApiUrl("https://github.com/owner/repo")).thenReturn(true);
        Mockito.when(gitHubApiProcess.checkUpdate("https://github.com/owner/repo"))
                .thenReturn(answer);
        Mockito.when(linkRepository.getUpdateTime(1L)).thenReturn(sameTime);

        // when

        updateCheckService.checkLinkUpdate(1L, "https://github.com/owner/repo");

        // then

        Mockito.verify(linkRepository, Mockito.never()).setUpdateTime(Mockito.anyLong(), Mockito.any());
        Mockito.verify(linkRepository, Mockito.never()).setDescription(Mockito.anyLong(), Mockito.any());
        Mockito.verify(updateNotifyService, Mockito.never())
                .notifyChatsForLink(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void checkUpdateCheckValidApiProcess() {
        // given

        OffsetDateTime updateTime = OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        ApiAnswer answer = new ApiAnswer("valid description", updateTime);

        Mockito.when(stackOverflowApiProcess.isApiUrl("https://stackoverflow.com/q/123"))
                .thenReturn(true);
        Mockito.when(gitHubApiProcess.isApiUrl("https://stackoverflow.com/q/123"))
                .thenReturn(false);
        Mockito.when(stackOverflowApiProcess.checkUpdate("https://stackoverflow.com/q/123"))
                .thenReturn(answer);

        // when

        ApiAnswer result = updateCheckService.checkUpdate("https://stackoverflow.com/q/123");

        // then

        assertNotNull(result);
        assertEquals("valid description", result.description());
        Mockito.verify(stackOverflowApiProcess).checkUpdate("https://stackoverflow.com/q/123");
    }

    @Test
    void checkUpdateCheckNoMatchingApiProcess() {
        // given

        Mockito.when(gitHubApiProcess.isApiUrl("https://test.com")).thenReturn(false);
        Mockito.when(stackOverflowApiProcess.isApiUrl("https://test.com")).thenReturn(false);

        // when

        ApiAnswer result = updateCheckService.checkUpdate("https://test.com");

        // then

        assertNull(result);
    }
}
