package backend.academy.bot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient scrapperWebClient() {
        return WebClient.builder()
            .baseUrl("http://localhost:8081") // Базовый URL Scrapper API
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
