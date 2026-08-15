package lyra;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class TokenManager {
    private final String clientId;
    private final String clientSecret;

    public TokenManager(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String refreshAccessToken(String refreshToken) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String credentials = clientId + ":" + clientSecret;

        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=refresh_token&" +
                                "refresh_token=" + refreshToken
                ))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());
        }

        return response.body();
    }

    public void saveTokensAndExpiresIn(String accessToken, String refreshToken, long expiresIn) throws IOException {
        Properties token = new Properties();

        token.load(new FileReader("config/Tokens.properties"));

        if (accessToken != null) {
            token.setProperty("access_token", accessToken);
        }

        if (refreshToken != null) {
            token.setProperty("refresh_token", refreshToken);
        }

        token.setProperty("expires_in", String.valueOf(expiresIn));

        token.store(new FileWriter("config/Tokens.properties"), null);
    }


    public String loadAccessToken() throws IOException {
        Properties token = new Properties();

        token.load(new FileReader("config/Tokens.properties"));

        String accessToken = token.getProperty("access_token");

        return accessToken;
    }

    public String loadRefreshToken() throws IOException {
        Properties token = new Properties();

        token.load(new FileReader("config/Tokens.properties"));

        String refreshToken = token.getProperty("refresh_token");

        return refreshToken;
    }

    public long loadExpiresIn() throws IOException {
        Properties token = new Properties();

        token.load(new FileReader("config/Tokens.properties"));

        String loadedString = token.getProperty("expires_in");

        long expiresIn = Long.parseLong(loadedString);

        return expiresIn;
    }
}
