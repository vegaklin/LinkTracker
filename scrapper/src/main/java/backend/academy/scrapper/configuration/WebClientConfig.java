package backend.academy.scrapper.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    public static final String BOT_WEB_CLIENT = "botWebClient";
    public static final String GITHUB_WEB_CLIENT = "githubWebClient";
    public static final String STACKOVERFLOW_WEB_CLIENT = "stackOverflowWebClient";

    @Bean(BOT_WEB_CLIENT)
    public WebClient botWebClient(BotConfig botConfig) {
        return WebClient.builder()
                .baseUrl(botConfig.url())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(GITHUB_WEB_CLIENT)
    public WebClient githubWebClient(@Value("${api.github.base-url}") String githubBaseUrl) {
        return WebClient.builder()
                .baseUrl(githubBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(STACKOVERFLOW_WEB_CLIENT)
    public WebClient stackOverflowWebClient(@Value("${api.stackoverflow.base-url}") String stackoverflowBaseUrl) {
        return WebClient.builder()
                .baseUrl(stackoverflowBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
