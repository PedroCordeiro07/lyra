package lyra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Search {
    private String accessToken;

    public Search(String accessToken) {
        this.accessToken = accessToken;
    }

    public String searchTrack(String accessToken, String query) throws JsonProcessingException {

        query = query.replace(" ", "%20");

        HttpClient  client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q=%s".formatted(query)))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        StringBuilder tracks = new StringBuilder();

        for (JsonNode tracks : root.get("tracks")) {
            String name = tracks.get("name").asText();
            String id = tracks.get("id").asText();

            devices.append(name)
                    .append(" - ")
                    .append(id)
                    .append("\n");
        }

        // TO DO - SEARCH METHODS

    }
}
