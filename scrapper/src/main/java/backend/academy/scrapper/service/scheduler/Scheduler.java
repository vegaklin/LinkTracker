package backend.academy.scrapper.service.scheduler;

import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.model.Link;
import backend.academy.scrapper.service.UpdateCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class Scheduler {

    private final LinkRepository linkRepository;

    private final UpdateCheckService updateCheckService;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    @Value("${scheduler.thread-count}")
    private int threadCount;

    @Value("${scheduler.thread-waiting-time}")
    private int threadWaitingTime;

    @Scheduled(fixedRateString = "${scheduler.fixed-interval-ms}")
    public void checkForUpdates() {
        log.info("Starting scheduled link update check with batch size {} and {} threads", batchSize, threadCount);

        Long allLinksCount = linkRepository.countLinks();
        int offset = 0;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            while (offset < allLinksCount) {
                List<Link> batch = linkRepository.getLinks(batchSize, offset);
                if (batch.isEmpty()) {
                    break;
                }

                int currentBatchSize = Math.max(1, batch.size() / threadCount);
                for (int i = 0; i < batch.size(); i += currentBatchSize) {
                    List<Link> subBatch = batch.subList(i, Math.min(i + currentBatchSize, batch.size()));
                    executor.execute(() -> processSubBatch(subBatch));
                }
                offset += batchSize;
            }
        } finally {
            shutdownExecutor(executor);
        }
    }

    private void processSubBatch(List<Link> currentBatch) {
        currentBatch.forEach(link -> updateCheckService.checkLinkUpdate(link.id(), link.url()));
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(threadWaitingTime, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    log.error("Threads did not terminate in time");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Thread pool termination interrupted", e);
        }
    }
}
