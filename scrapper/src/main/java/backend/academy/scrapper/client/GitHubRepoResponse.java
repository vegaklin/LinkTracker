package backend.academy.scrapper.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubRepoResponse(Long id, String name, @JsonProperty("updated_at") String updatedAt) {}
