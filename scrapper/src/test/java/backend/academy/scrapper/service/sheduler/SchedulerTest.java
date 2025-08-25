// package backend.academy.scrapper.service.sheduler;
//
// import backend.academy.scrapper.repository.LinkRepository;
// import backend.academy.scrapper.repository.model.Link;
// import backend.academy.scrapper.service.UpdateCheckService;
// import backend.academy.scrapper.service.scheduler.Scheduler;
// import java.time.OffsetDateTime;
// import java.time.ZoneOffset;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// class SchedulerTest {
//
//    @Mock
//    private LinkRepository linkRepository;
//
//    @Mock
//    private UpdateCheckService updateCheckService;
//
//    @InjectMocks
//    private Scheduler scheduler;
//
//    @Test
//    void checkCheckForUpdatesWithLinks() {
//        // given
//
//        Map<Long, Link> links = new HashMap<>();
//        links.put(
//                1L,
//                new Link(
//                        "https://test.ru/1",
//                        List.of("tag1"),
//                        List.of("filter:filter1"),
//                        OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC)));
//        links.put(
//                2L,
//                new Link(
//                        "https://test.ru/2",
//                        List.of("tag2"),
//                        List.of("filter:filter2"),
//                        OffsetDateTime.of(2025, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC)));
//        Mockito.when(linkRepository.getLinks()).thenReturn(links);
//
//        // when
//
//        scheduler.checkForUpdates();
//
//        // then
//
//        Mockito.verify(linkRepository).getLinks();
//        Mockito.verify(updateCheckService).checkLinkUpdate(1L, "https://test.ru/1");
//        Mockito.verify(updateCheckService).checkLinkUpdate(2L, "https://test.ru/2");
//        Mockito.verifyNoMoreInteractions(updateCheckService);
//    }
//
//    @Test
//    void checkCheckForUpdatesNoLinks() {
//        // given
//
//        Mockito.when(linkRepository.getLinks()).thenReturn(new HashMap<>());
//
//        // when
//
//        scheduler.checkForUpdates();
//
//        // then
//
//        Mockito.verify(linkRepository).getLinks();
//        Mockito.verifyNoInteractions(updateCheckService);
//    }
// }
