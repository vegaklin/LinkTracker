package backend.academy.scrapper.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient botWebClient() {
        return WebClient.builder()
            .baseUrl("http://localhost:8080") // Базовый URL Bot API
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
