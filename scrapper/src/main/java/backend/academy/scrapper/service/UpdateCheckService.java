package backend.academy.scrapper.service;

import backend.academy.scrapper.client.dto.ApiAnswer;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.api.ApiProcess;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCheckService {

    private final LinkRepository linkRepository;

    private final List<ApiProcess> apiProcessList;

    private final UpdateNotifyService updateNotifyService;

    public void checkLinkUpdate(Long linkId, String url) {
        log.info("Checking for updates for linkId: {}, url: {}", linkId, url);

        ApiAnswer apiAnswer = checkUpdate(url);
        if (apiAnswer != null) {
            OffsetDateTime previousUpdate = linkRepository.getUpdateTime(linkId);
            OffsetDateTime currentUpdate = apiAnswer.lastUpdate();

            if (previousUpdate != null && currentUpdate.isAfter(previousUpdate)) {
                log.info("Update detected for linkId: {}, url: {}", linkId, url);

                linkRepository.setUpdateTime(linkId, currentUpdate);
                linkRepository.setDescription(linkId, apiAnswer.description());

                updateNotifyService.notifyChatsForLink(linkId, url, apiAnswer.description());
            } else {
                log.info("No update for linkId: {}, url: {}", linkId, url);
            }
        } else {
            log.info("No update time received for linkId: {}", linkId);
        }
    }

    public ApiAnswer checkUpdate(String url) {
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
        return null;
    }
}
