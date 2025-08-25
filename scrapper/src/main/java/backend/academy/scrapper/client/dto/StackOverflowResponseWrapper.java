package backend.academy.scrapper.client.dto;

import java.util.List;

public record StackOverflowResponseWrapper(List<StackOverflowResponse> items)
        implements ListWrapper<StackOverflowResponse> {}
