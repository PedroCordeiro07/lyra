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

public class SearchTrack {

    public record TrackInfo(
            String id,
            String name,
            List<String> artists,
            String album,
            String albumCover,
            long durationMs,
            String uri,
            boolean isPlayable
    ) {}

    public String searchTrack(String accessToken, String query) throws IOException, InterruptedException {

        query = query.replace(" ", "%20");

        HttpClient  client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q=" + query + "&type=track"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }

    public List<TrackInfo> parseTrackResults(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode items = root.path("tracks").path("items");

        List<TrackInfo> tracks = new ArrayList<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                String id = item.path("id").asText();
                String name = item.path("name").asText();
                String uri = item.path("uri").asText();
                long durationMs = item.path("duration_ms").asLong();
                boolean isPlayable = item.path("is_playable").asBoolean(false);

                List<String> artists = new ArrayList<>();
                for (JsonNode artistNode : item.path("artists")) {
                    artists.add(artistNode.path("name").asText());
                }

                JsonNode albumNode = item.path("album");
                String album = albumNode.path("name").asText();

                String albumCover = "";
                JsonNode imagesNode = albumNode.path("images");
                if (imagesNode != null && imagesNode.isArray() && !imagesNode.isEmpty()) {
                    albumCover = imagesNode.path(0).path("url").asText(); // largest image, Spotify lists biggest first
                }

                tracks.add(new TrackInfo(id, name, artists, album, albumCover, durationMs, uri, isPlayable));
            }
        }

        return tracks;
    }


    public String formatTrackResults(List<TrackInfo> tracks) {
        StringBuilder formattedList = new StringBuilder();

        for (TrackInfo track : tracks) {
            formattedList.append(track.toString());
            formattedList.append("\n");
        }
        return formattedList.toString();
    }
}
