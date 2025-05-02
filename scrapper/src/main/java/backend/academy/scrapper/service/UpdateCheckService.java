package backend.academy.scrapper.service;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.service.api.ApiProcess;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCheckService {

    private final LinkRepository linkRepository;

    private final List<ApiProcess> apiProcessList;

    private final UpdateSenderService updateSenderService;

    public void checkLinkUpdate(Long linkId, String url) {
        log.info("Checking for updates for linkId: {}, url: {}", linkId, url);

        OffsetDateTime lastUpdate = checkUpdate(url).block();
        if (lastUpdate != null) {
            OffsetDateTime previousUpdate = linkRepository.getUpdateTime(linkId);
            if (previousUpdate != null && lastUpdate.isAfter(previousUpdate)) {
                log.info("Update detected for linkId: {}, url: {}", linkId, url);

                linkRepository.setUpdateTime(linkId, lastUpdate);
                updateSenderService.notifyChatsForLink(linkId, url);
            } else {
                log.info("No update for linkId: {}, url: {}", linkId, url);
            }
        } else {
            log.info("No update time received for linkId: {}", linkId);
        }
    }

    public Mono<OffsetDateTime> checkUpdate(String url) {
        log.info("Checking update for url: {}", url);

        for (ApiProcess apiProcess : apiProcessList) {
            if (apiProcess.isApiUrl(url)) {
                log.info(
                        "API process found for url: {}, processing with {}",
                        url,
                        apiProcess.getClass().getSimpleName());

                return apiProcess.checkUpdate(url);
            }
        }

        log.warn("No API process found for url: {}", url);
        return Mono.empty();
    }
}
