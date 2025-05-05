package backend.academy.scrapper.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubResponse(@JsonProperty("title") String title,
                             @JsonProperty("user") GitHubUser user,
                             @JsonProperty("created_at") OffsetDateTime createdAt,
                             @JsonProperty("body") String body
) {
    public String getUser() {
        return user != null ? user.login() : "Unknown";
    }

    public String getBody() {
        if (body == null || body.isEmpty()) {
            return "";
        }
        return body.length() > 200 ? body.substring(0, 200) + "..." : body;
    }

    public String toMessage() {
        return String.format("""
            Название Issue: %s
            Пользователь: %s
            Время создания: %s
            Описание: %s
            """,
            title, getUser(), createdAt.toString(), getBody());
    }

    public record GitHubUser(
        @JsonProperty("login") String login
    ) {}
}
