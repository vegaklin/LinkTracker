package backend.academy.scrapper.client.dto;

import java.time.OffsetDateTime;

public record ApiAnswer(String description, OffsetDateTime lastUpdate) {
}
