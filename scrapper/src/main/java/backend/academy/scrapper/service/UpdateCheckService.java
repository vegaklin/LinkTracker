package backend.academy.scrapper.service;

import java.time.OffsetDateTime;
import java.util.List;
import backend.academy.scrapper.repository.link.LinkRepository;
import backend.academy.scrapper.service.api.ApiProcess;
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
        OffsetDateTime lastUpdate = checkUpdate(url).block();
        if (lastUpdate != null) {
            OffsetDateTime previousUpdate = linkRepository.getUpdateTime(linkId);
            if (lastUpdate.isAfter(previousUpdate)) {
                log.info("Update detected for linkId: {}, url: {}", linkId, url);
                linkRepository.setUpdateTime(linkId, lastUpdate);
                updateSenderService.notifyChatsForLink(linkId, url);
            }
        } else {
            log.warn("No update time received for linkId: {}", linkId);
        }
    }

    public Mono<OffsetDateTime> checkUpdate(String url) {
        for (ApiProcess apiProcess : apiProcessList) {
            if (apiProcess.isApiUrl(url)) {
                return apiProcess.checkUpdate(url);
            }
        }
        return Mono.empty();
    }
}
