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

public class SearchArtist {

    public record ArtistInfo(
            String id,
            String name,
            String image,
            List<String> genres,
            int popularity,
            long followers,
            String uri
    ) {}


    public String searchArtist(String accessToken, String query) throws IOException, InterruptedException {

        query = query.replace(" ", "%20");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q=%s&type=artist".formatted(query)))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }


    public List<ArtistInfo> parseArtistResults(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode items = root.path("artists").path("items");

        List<ArtistInfo> artists = new ArrayList<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                if (item == null || item.isNull()) continue;

                String id = item.path("id").asText();
                String name = item.path("name").asText();
                String uri = item.path("uri").asText();
                int popularity = item.path("popularity").asInt(0);
                long followers = item.path("followers").path("total").asLong(0);

                List<String> genres = new ArrayList<>();
                JsonNode genresNode = item.path("genres");
                if (genresNode != null && genresNode.isArray()) {
                    for (JsonNode genreNode : genresNode) {
                        genres.add(genreNode.asText());
                    }
                }

                String image = "";
                JsonNode imagesNode = item.get("images");
                if (imagesNode != null && imagesNode.isArray() && !imagesNode.isEmpty()) {
                    image = imagesNode.path(0).path("url").asText();
                }

                artists.add(new ArtistInfo(id, name, image, genres, popularity, followers, uri));
            }
        }

        return artists;
    }


    // TO DO - toString()
    public String formatArtistsResults(List<ArtistInfo> artists) {
        StringBuilder formattedList = new StringBuilder();

        for (ArtistInfo artist : artists) {
            formattedList.append(artist.toString());
        }
        return formattedList.toString();
    }

}
