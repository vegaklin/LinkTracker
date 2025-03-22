package backend.academy.scrapper.repository.link.model;


import java.util.List;

public record Link(String url, List<String> tags, List<String> filters) {}
