package backend.academy.scrapper.service.api;

import reactor.core.publisher.Mono;
import java.time.OffsetDateTime;

public interface ApiProcess {
    boolean isApiUrl(String url);
    Mono<OffsetDateTime> checkUpdate(String url);
}
