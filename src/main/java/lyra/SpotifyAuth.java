package lyra;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SpotifyAuth {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public SpotifyAuth(String clientId, String clientSecret,  String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public String getAppToken() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        String credentials = clientId + ":" + clientSecret;

        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }

    public String getAuthorizationUrl() {

        return "https://accounts.spotify.com/authorize"
                + "?client_id=" + clientId
                + "&response_type=code"
                + "&redirect_uri=" + redirectUri
                + "&scope=user-read-currently-playing%20user-modify-playback-state";
    }


    public String exchangeCodeForToken(String authorizationCode) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String credentials = clientId + ":" + clientSecret;

        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code" +
                                "&code=" + authorizationCode +
                                "&redirect_uri=http://127.0.0.1:3000/callback"
                ))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }

    public String parseRefreshToken(String JsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(JsonResponse);

        if (node.has("refresh_token")) {
            return node.get("refresh_token").asText();
        }

        return null;
    }

    public String parseAccessToken(String JsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(JsonResponse);

        if (node.has("access_token")) {
            return node.get("access_token").asText();
        }

        return null;
    }

    public long parseExpiresIn(String JsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(JsonResponse);

        return node.get("expires_in").asLong();
    }
}