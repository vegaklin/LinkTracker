package backend.academy.scrapper.service.api;

import java.time.OffsetDateTime;
import backend.academy.scrapper.client.dto.ApiAnswer;
import reactor.core.publisher.Mono;

public interface ApiProcess {
    boolean isApiUrl(String url);

    ApiAnswer checkUpdate(String url);
}
