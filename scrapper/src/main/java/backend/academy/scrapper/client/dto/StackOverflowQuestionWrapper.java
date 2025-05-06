package backend.academy.scrapper.client.dto;

import java.util.List;

public record StackOverflowQuestionWrapper (
    List<StackOverflowQuestion> items
) implements ListWrapper<StackOverflowQuestion>{}
