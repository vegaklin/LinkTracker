package backend.academy.scrapper.client;

import backend.academy.scrapper.client.dto.ListWrapper;
import backend.academy.scrapper.client.dto.StackOverflowQuestion;
import backend.academy.scrapper.client.dto.StackOverflowQuestionWrapper;
import backend.academy.scrapper.client.dto.StackOverflowResponse;
import backend.academy.scrapper.client.dto.StackOverflowResponseWrapper;
import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.configuration.WebClientConfig;
import backend.academy.scrapper.exception.ApiClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class StackOverflowClient {

    private final ScrapperConfig scrapperConfig;

    private final WebClient stackOverflowWebClient;

    public static final String QUESTION_ENDPOINT = "/2.3/questions/{questionId}";
    public static final String QUESTION_ANSWERS_ENDPOINT = QUESTION_ENDPOINT + "/answers";

    public static final String SITE_PARAM = "site";
    public static final String SITE_VALUE = "stackoverflow";
    public static final String KEY_PARAM = "key";
    public static final String ACCESS_TOKEN_PARAM = "access_token";
    public static final String FILTER_PARAM = "filter";
    public static final String FILTER_VALUE = "withbody";

    public StackOverflowClient(
            ScrapperConfig scrapperConfig,
            @Qualifier(WebClientConfig.STACKOVERFLOW_WEB_CLIENT) WebClient stackOverflowWebClient) {
        this.scrapperConfig = scrapperConfig;
        this.stackOverflowWebClient = stackOverflowWebClient;
    }

    public Mono<StackOverflowResponse> getQuestion(Long questionId) {
        Mono<StackOverflowResponse> answerMono = getAnswer(questionId);
        Mono<StackOverflowQuestion> questionTitleMono = getQuestionTitle(questionId);

        return Mono.zip(answerMono, questionTitleMono).map(tuple -> {
            StackOverflowResponse answer = tuple.getT1();
            StackOverflowQuestion questionTitle = tuple.getT2();
            return new StackOverflowResponse(
                    questionTitle.title(), answer.owner(), answer.creationDate(), answer.body());
        });
    }

    private Mono<StackOverflowResponse> getAnswer(Long questionId) {
        return stackOverflowWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(QUESTION_ANSWERS_ENDPOINT)
                        .queryParam(SITE_PARAM, SITE_VALUE)
                        .queryParam(KEY_PARAM, scrapperConfig.stackOverflow().key())
                        //                .queryParam(ACCESS_TOKEN_PARAM, scrapperConfig.stackOverflow().accessToken())
                        .queryParam(FILTER_PARAM, FILTER_VALUE)
                        .build(questionId))
                .exchangeToMono(response -> handleResponse(response, questionId, StackOverflowResponseWrapper.class));
    }

    private Mono<StackOverflowQuestion> getQuestionTitle(Long questionId) {
        return stackOverflowWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(QUESTION_ENDPOINT)
                        .queryParam(SITE_PARAM, SITE_VALUE)
                        .queryParam(KEY_PARAM, scrapperConfig.stackOverflow().key())
                        //                .queryParam(ACCESS_TOKEN_PARAM, scrapperConfig.stackOverflow().accessToken())
                        .build(questionId))
                .exchangeToMono(response -> handleResponse(response, questionId, StackOverflowQuestionWrapper.class));
    }

    private <T, W extends ListWrapper<T>> Mono<T> handleResponse(
            ClientResponse response, Long questionId, Class<W> wrapperClass) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(wrapperClass)
                    .map(wrapper -> {
                        if (wrapper.items().isEmpty()) {
                            log.warn("No items found for ID: {}", questionId);
                            throw new ApiClientException("Empty items list");
                        }
                        return wrapper.items().getFirst();
                    })
                    .doOnSuccess(result -> log.info("Fetched SO {}: {}", questionId, result));
        } else {
            return response.bodyToMono(String.class).map(error -> {
                int statusCode = response.statusCode().value();

                log.error("StackOverflow API error for {}: status={}: {}", questionId, statusCode, error);
                throw new ApiClientException(error);
            });
        }
    }
}
