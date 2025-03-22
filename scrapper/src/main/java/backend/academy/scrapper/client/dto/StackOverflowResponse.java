package backend.academy.scrapper.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record StackOverflowResponse(@JsonProperty("last_activity_date") long lastActivityDate) {
    public OffsetDateTime getLastActivityDateAsOffsetDateTime() {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(lastActivityDate), ZoneOffset.UTC);
    }
}
