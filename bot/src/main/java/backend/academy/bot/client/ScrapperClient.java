package backend.academy.bot.client;

import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ScrapperClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl = "http://localhost:8081";

    public void registerChat(long chatId) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/tg-chat/" + chatId))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public void addLink(long chatId, String link, String[] tags, String[] filters) {
        String body = String.format("{\"link\":\"%s\",\"tags\":%s,\"filters\":%s}",
            link, arrayToJson(tags), arrayToJson(filters));
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/links"))
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public String getLinks(long chatId) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/links"))
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .GET()
            .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body(); // Пока просто возвращаем как есть
        } catch (Exception e) {
            return "";
        }
    }

    public void removeLink(long chatId, String link) {
        String body = String.format("{\"link\":\"%s\"}", link);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/links"))
            .header("Tg-Chat-Id", String.valueOf(chatId))
            .header("Content-Type", "application/json")
            .method("DELETE", HttpRequest.BodyPublishers.ofString(body))
            .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private String arrayToJson(String[] arr) {
        return "[" + String.join(",", arr) + "]";
    }
}
