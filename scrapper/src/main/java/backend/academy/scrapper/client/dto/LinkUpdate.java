package backend.academy.scrapper.client.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record LinkUpdate(
    @NotNull Long id,
    @NotEmpty String url,
    String description,
    @NotNull List<Long> tgChatIds
) {}
