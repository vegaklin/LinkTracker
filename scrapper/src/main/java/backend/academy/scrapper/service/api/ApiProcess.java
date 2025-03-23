package backend.academy.scrapper.service.api;

import java.time.OffsetDateTime;
import reactor.core.publisher.Mono;

public interface ApiProcess {
    boolean isApiUrl(String url);
    Mono<OffsetDateTime> checkUpdate(String url);
}
