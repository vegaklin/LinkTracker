package backend.academy.scrapper.service.sender;

import backend.academy.scrapper.client.BotClient;
import backend.academy.scrapper.client.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HttpUpdateSenderService implements UpdateSenderService{

    private final BotClient botClient;

    @Override
    public void sendUpdate(LinkUpdate update) {
        botClient.sendUpdate(update).block();
    }
}
