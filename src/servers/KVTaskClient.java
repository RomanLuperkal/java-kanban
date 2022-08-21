package servers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private final String API_TOKEN;
    private final String kVServerUrl;

    public KVTaskClient(String url) throws InterruptedException, IOException {
        kVServerUrl = url;
        API_TOKEN = getAPI_TOKEN();
    }

    public String load(String key) throws InterruptedException, IOException {
        URI uri = URI.create(kVServerUrl + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, handler);
        return response.body();
    }

    public void put(String key, String json) throws InterruptedException, IOException {
        URI uri = URI.create(kVServerUrl + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpClient client = HttpClient.newHttpClient();
        client.send(request, handler);
    }

    private String getAPI_TOKEN() throws InterruptedException, IOException {
        URI uri = URI.create(kVServerUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, handler);
        return response.body();
    }
}
