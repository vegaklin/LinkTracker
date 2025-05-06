package backend.academy.scrapper.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record StackOverflowResponse(String questionTitle,
                                    @JsonProperty("owner") StackOverflowOwner owner,
                                    @JsonProperty("creation_date") Long creationDate,
                                    @JsonProperty("body") String body
) {
    public OffsetDateTime getLastActivityDateAsOffsetDateTime() {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(creationDate), ZoneOffset.UTC);
    }

    public String getUser() {
        return owner != null ? owner.displayName() : "Unknown";
    }

    public String getBody() {
        if (body == null || body.isEmpty()) {
            return "";
        }
        return body.length() > 200 ? body.substring(0, 200) + "..." : body;
    }

    public String toMessage() {
        return String.format("""
            Название Answer: %s
            Пользователь: %s
            Время создания: %s
            Описание: %s
            """,
            questionTitle, getUser(), getLastActivityDateAsOffsetDateTime().toString(), getBody());
    }

    public record StackOverflowOwner(
        @JsonProperty("display_name") String displayName
    ) {}
}
