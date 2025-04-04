package backend.academy.scrapper.repository.model;

import java.time.OffsetDateTime;

public record Link(Long id, String url, String description, OffsetDateTime updateTime) {}
