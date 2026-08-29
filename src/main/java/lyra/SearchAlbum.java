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

public class SearchAlbum {

    public record AlbumInfo(
            String id,
            String name,
            List<String> artists,
            String cover,
            String releaseDate,
            int totalTracks,
            String uri,
            String albumType
    ) {}



    public String searchAlbum(String accessToken, String query) throws IOException, InterruptedException {

        query = query.replace(" ", "%20");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q=%s&type=album".formatted(query)))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }


    public List<AlbumInfo> parseAlbumResults(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode items = root.path("albums").path("items");

        List<AlbumInfo> albums = new ArrayList<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                if (item == null || item.isNull()) continue;

                String id = item.path("id").asText();
                String name = item.path("name").asText();
                String uri = item.path("uri").asText();
                String releaseDate = item.path("release_date").asText("");
                int totalTracks = item.path("total_tracks").asInt(0);
                String albumType = item.path("album_type").asText("");

                List<String> artists = new ArrayList<>();
                JsonNode artistsNode = item.path("artists");
                if (artistsNode != null && artistsNode.isArray()) {
                    for (JsonNode artistNode : artistsNode) {
                        artists.add(artistNode.path("name").asText());
                    }
                }

                String cover = "";
                JsonNode imagesNode = item.path("images");
                if (imagesNode != null && imagesNode.isArray() && !imagesNode.isEmpty()) {
                    cover = imagesNode.path(0).path("url").asText();
                }

                albums.add(new AlbumInfo(id, name, artists, cover, releaseDate, totalTracks, uri, albumType));
            }
        }

        return albums;
    }

    // TO DO - toString()
    public String formatArtistsResults(List<AlbumInfo> albums) {
        StringBuilder formattedList = new StringBuilder();

        for (AlbumInfo album : albums) {
            formattedList.append(album.toString());
        }
        return formattedList.toString();
    }

}
