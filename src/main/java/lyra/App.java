package lyra;

import java.util.Properties;
import java.io.IOException;

/**
 * Lyra, the best Spotify agent to ever exist!
 *
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {

        Properties config = ConfigLoader.loadProperties();

        String clientId = config.getProperty("clientId");
        String clientSecret = config.getProperty("clientSecret");
        String redirectUri = config.getProperty("redirectUri");

        SpotifyAuth auth = new SpotifyAuth(clientId, clientSecret,  redirectUri);

        // 1 - Start callback server
        CallBackServer callback = new CallBackServer();
        callback.start();

        // 2 - Generate Spotify login URL
        String url = auth.getAuthorizationUrl();

        System.out.println("Open this URL to link Lyra to your account:");
        System.out.println(url);

        // 3 - Wait for Spotify callback
        String authorizationCode = callback.getAuthorizationCode();

        // 4 - Exchange authorization code for tokens
        String tokenResponse = auth.exchangeCodeForToken(authorizationCode);

        String accessToken = auth.parseAccessToken(tokenResponse);
        String refreshToken = auth.parseRefreshToken(tokenResponse);
        long expiresIn = auth.parseExpiresIn(tokenResponse);

        // 5 - Use Spotify API
        SpotifyClient spotify = new SpotifyClient(accessToken);

        // 6 - Save tokens for future use
        TokenManager tokenmanager = new TokenManager(clientId, clientSecret);

        tokenmanager.saveTokensAndExpiresIn(accessToken, refreshToken, expiresIn);

        String loadedAccessToken = tokenmanager.loadAccessToken();
        String loadedRefreshToken = tokenmanager.loadRefreshToken();
        long loadExpiresIn = tokenmanager.loadExpiresIn();

        System.out.println(loadedAccessToken);
        System.out.println(loadedRefreshToken);
        System.out.println(loadExpiresIn);
    }
}
