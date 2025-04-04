package backend.academy.scrapper.service.scheduler;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.service.UpdateCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class Scheduler {

    private final LinkRepository linkRepository;

    private final UpdateCheckService updateCheckService;

    @Scheduled(fixedRate = 10000)
    public void checkForUpdates() {
        log.info("Starting scheduled link update check");

        linkRepository.getLinks().forEach(link -> {
            updateCheckService.checkLinkUpdate(link.id(), link.url());
        });
    }
}
