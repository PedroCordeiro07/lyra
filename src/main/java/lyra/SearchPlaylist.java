package lyra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class SearchPlaylist {

    public record PlaylistInfo(
            String id,
            String name,
            String description,
            String cover,
            String owner,
            int totalTracks,
            String uri,
            boolean isPublic,
            boolean collaborative
    ) {}

    public String searchPlaylist(String accessToken, String query) throws IOException, InterruptedException {

        query = query.replace(" ", "%20");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q=%s&type=playlist".formatted(query)))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }


    public List<PlaylistInfo> parsePlaylistResults(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode items = root.path("playlists").path("items");

        List<PlaylistInfo> playlists = new ArrayList<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                if (item == null || item.isNull()) continue; // Spotify sometimes returns null entries here

                String id = item.path("id").asText();
                String name = item.path("name").asText();
                String uri = item.path("uri").asText();
                boolean isPublic = item.path("public").asBoolean(false);
                boolean collaborative = item.path("collaborative").asBoolean(false);

                String description = item.path("description").asText("");

                String owner = item.path("owner").path("display_name").asText("");

                int totalTracks = item.path("tracks").path("total").asInt(0);

                String cover = "";
                JsonNode imagesNode = item.get("images");
                if (imagesNode != null && imagesNode.isArray() && !imagesNode.isEmpty()) {
                    cover = imagesNode.path(0).path("url").asText();
                }

                playlists.add(new PlaylistInfo(id, name, description, cover, owner, totalTracks, uri, isPublic, collaborative));
            }
        }

        return playlists;
    }

    // TO DO - toString()
    public String formatArtistsResults(List<PlaylistInfo> playlists) {
        StringBuilder formattedList = new StringBuilder();

        for (PlaylistInfo playlist : playlists) {
            formattedList.append(playlist.toString());
        }
        return formattedList.toString();
    }

}
