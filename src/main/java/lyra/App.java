package lyra;

import java.util.List;
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

        CallBackServer callback = new CallBackServer();
        callback.start();

        String url = auth.getAuthorizationUrl();

        System.out.println("Open this URL to link Lyra to your account:");
        System.out.println(url);

        String authorizationCode = callback.getAuthorizationCode();

        String tokenResponse = auth.exchangeCodeForToken(authorizationCode);

        String accessToken = auth.parseAccessToken(tokenResponse);
        String refreshToken = auth.parseRefreshToken(tokenResponse);
        long expiresIn = auth.parseExpiresIn(tokenResponse);


        TokenManager tokenmanager = new TokenManager(clientId, clientSecret);

        tokenmanager.saveTokensAndExpiresIn(accessToken, refreshToken, expiresIn);

        String loadedAccessToken = tokenmanager.loadAccessToken();

        Playback playback = new Playback(loadedAccessToken);


        Device device = new Device(loadedAccessToken);

        SearchTrack searchTrack = new SearchTrack();

        String loadedRefreshToken = tokenmanager.loadRefreshToken();

        String newTokenResponse = tokenmanager.refreshAccessToken(loadedRefreshToken);

        String newAccessToken = auth.parseAccessToken(newTokenResponse);
        long newExpiresIn = auth.parseExpiresIn(newTokenResponse);

        tokenmanager.saveTokensAndExpiresIn(newAccessToken, loadedRefreshToken, newExpiresIn);

        loadedAccessToken =  tokenmanager.loadAccessToken();

        while (true) {
            System.out.println("Device controls:");
            System.out.println("1. To transfer the playback");
            System.out.println("2. To check playback state");
            System.out.println("0. Quit");

            int choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                String jsonResponse = device.getAvailableDevices(accessToken);
                List<Device.DeviceInfo> availableDevices = device.parseAvailableDevices(jsonResponse);
                System.out.println(availableDevices);

                System.out.println("Enter the device ID you want to transfer to:");
                String deviceId = input.nextLine();

                String action = device.transferToDevice(loadedAccessToken, deviceId);
                System.out.println(action);
            }

            else if (choice == 2) {
                String playbackState = device.getPlaybackState(loadedAccessToken);
                System.out.println(playbackState);
            }

            else if (choice == 0) {
                System.out.println("Ending device controls...");
                break;
            }
        }


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
                String action = playback.pause(loadedAccessToken);
                System.out.println(action);
            }

            else if (choice == 2) {
                String action = playback.resume(loadedAccessToken);
                System.out.println(action);
            }

            else if (choice == 3) {
                String action = playback.skipToNext(loadedAccessToken);
                System.out.println(action);
            }

            else if (choice == 4) {
                String action = playback.skipToPrevious(loadedAccessToken);
                System.out.println(action);
            }

            else if (choice == 5) {
                System.out.println("Enter the position you want to seek to (ms): ");
                int position = input.nextInt();

                String action = playback.seekToPosition(loadedAccessToken, position);
                System.out.println(action);
            }

            else if (choice == 6) {
                System.out.println("1. Repeat context ");
                System.out.println("2. Repeat track ");
                System.out.println("3. Repeat off ");
                int repeatChoice = input.nextInt();

                if (repeatChoice == 1) {
                    String action = playback.setRepeatModeContext(loadedAccessToken);
                    System.out.println(action);
                }

                else if (repeatChoice == 2) {
                    String action = playback.setRepeatModeTrack(loadedAccessToken);
                    System.out.println(action);
                }

                else if (repeatChoice == 3) {
                    String action = playback.setRepeatModeOff(loadedAccessToken);
                    System.out.println(action);
                }
            }

            else if (choice == 7) {
                System.out.println("Enter the volume you want to set: ");
                int volume = input.nextInt();

                String action = playback.setVolume(loadedAccessToken, volume);
                System.out.println(action);
            }

            else if (choice == 8) {
                System.out.println("1. Shuffle on ");
                System.out.println("2. Shuffle off ");
                int shuffleChoice = input.nextInt();

                if (shuffleChoice == 1) {
                    String action = playback.shuffleModeOn(loadedAccessToken);
                    System.out.println(action);
                }

                else if (shuffleChoice == 2) {
                    String action = playback.shuffleModeOff(loadedAccessToken);
                    System.out.println(action);
                }
            }

            else if (choice == 9) {
                String query = "Grupo Menos é Mais";
                String action = searchTrack.searchTrack(loadedAccessToken, query);

                List<SearchTrack.TrackInfo> tracks = searchTrack.parseTrackResults(action);

                String formattedResponse = searchTrack.formatTrackResults(tracks);
                System.out.println(formattedResponse);
            }

            else if (choice == 0) {
                System.out.println("Ending playback controls");
                break;
            }
        }
    }
}
