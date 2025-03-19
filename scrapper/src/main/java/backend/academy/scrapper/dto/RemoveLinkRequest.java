package backend.academy.scrapper.dto;

import jakarta.validation.constraints.NotEmpty;

public record RemoveLinkRequest(@NotEmpty String link) {}
