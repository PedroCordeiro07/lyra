package lyra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Search {
    private String accessToken;

    public Search(String accessToken) {
        this.accessToken = accessToken;
    }

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

    public List<TrackInfo> parseSearchResults(String jsonResponse) throws IOException {
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
    
}
