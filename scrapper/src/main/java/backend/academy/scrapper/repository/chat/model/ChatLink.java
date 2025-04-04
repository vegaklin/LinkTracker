package backend.academy.scrapper.repository.chat.model;

import java.util.List;

public record ChatLink(Long chat_id, Long link_id, List<String> tags, List<String> filters) {}
