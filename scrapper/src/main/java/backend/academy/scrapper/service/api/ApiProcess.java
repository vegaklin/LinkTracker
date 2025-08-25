package backend.academy.scrapper.service.api;

import backend.academy.scrapper.client.dto.ApiAnswer;

public interface ApiProcess {
    boolean isApiUrl(String url);

    ApiAnswer checkUpdate(String url);
}
