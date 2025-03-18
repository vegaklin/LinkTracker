package backend.academy.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "scrapper", ignoreUnknownFields = false)
public record ScrapperConfig(
    @NotEmpty String url
) {}
