package backend.academy.scrapper.repository.model;

import java.util.List;

public record ChatLink(Long chatId, Long linkId, List<String> tags, List<String> filters) {}
