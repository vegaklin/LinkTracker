package backend.academy.scrapper.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StackOverflowQuestion(
    @JsonProperty("title") String title
) {}
