package lyra;

import java.util.Properties;
import java.io.IOException;
import java.util.Scanner;

/**
 * Lyra, the best Spotify agent to ever exist!
 *
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner input = new Scanner(System.in);

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

        String refreshedAccessToken = tokenmanager.refreshAccessToken(refreshToken);

        tokenmanager.saveTokensAndExpiresIn(accessToken, refreshToken, expiresIn);

        String loadedAccessToken = tokenmanager.loadAccessToken();

        Playback playback = new Playback(loadedAccessToken);

        while (true) {
            System.out.println("Playback Controls:");
            System.out.println("1. Pause");
            System.out.println("2. Resume");
            System.out.println("3. Next");
            System.out.println("4. Previous");
            System.out.println("5. Seek to position");
            System.out.println("6. Repeat mode");
            System.out.println("7. Volume");
            System.out.println("8. Shuffle");
            System.out.println("9. Quit");

            int choice = input.nextInt();

            if (choice == 1) {
                String action = playback.pause(accessToken);
                System.out.println(action);
            }

            else if (choice == 2) {
                String action = playback.resume(accessToken);
                System.out.println(action);
            }

            else if (choice == 3) {
                String action = playback.skipToNext(accessToken);
                System.out.println(action);
            }

            else if (choice == 4) {
                String action = playback.skipToPrevious(accessToken);
                System.out.println(action);
            }

            else if (choice == 5) {
                System.out.println("Enter the position you want to seek to (ms): ");
                int position = input.nextInt();

                String action = playback.seekToPosition(accessToken, position);
                System.out.println(action);
            }

            else if (choice == 6) {
                System.out.println("1. Repeat context ");
                System.out.println("2. Repeat track ");
                System.out.println("3. Repeat off ");
                int repeatChoice = input.nextInt();

                if (repeatChoice == 1) {
                    String action = playback.setRepeatModeContext(accessToken);
                    System.out.println(action);
                }

                else if (repeatChoice == 2) {
                    String action = playback.setRepeatModeTrack(accessToken);
                    System.out.println(action);
                }

                else if (repeatChoice == 3) {
                    String action = playback.setRepeatModeOff(accessToken);
                    System.out.println(action);
                }
            }

            else if (choice == 7) {
                System.out.println("Enter the volume you want to set: ");
                int volume = input.nextInt();

                String action = playback.setVolume(accessToken, volume);
                System.out.println(action);
            }

            else if (choice == 8) {
                System.out.println("1. Shuffle on ");
                System.out.println("2. Shuffle off ");
                int shuffleChoice = input.nextInt();

                if (shuffleChoice == 1) {
                    String action = playback.shuffleModeOn(accessToken);
                    System.out.println(action);
                }

                else if (shuffleChoice == 2) {
                    String action = playback.shuffleModeOff(accessToken);
                    System.out.println(action);
                }
            }

            else if (choice == 9) {
                System.out.println("Ending playback controls");
                break;
            }
        }
    }
}
