package backend.academy.scrapper.service.sender;

import backend.academy.scrapper.client.dto.LinkUpdate;

public interface UpdateSenderService {
    void sendUpdate(LinkUpdate update);
}
