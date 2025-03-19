package backend.academy.bot.client.dto;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, int size) {}
