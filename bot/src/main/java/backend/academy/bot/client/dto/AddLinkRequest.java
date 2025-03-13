package backend.academy.bot.client.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddLinkRequest(
    @NotEmpty String link,
    List<String> tags,
    List<String> filters
) {}
