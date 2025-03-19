package backend.academy.bot.client.dto;

import jakarta.validation.constraints.NotEmpty;

public record RemoveLinkRequest(@NotEmpty String link) {}
