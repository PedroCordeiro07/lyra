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

public class Device {
    private final String accessToken;

    public Device(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAvailableDevices(String accessToken) throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/me/player/devices"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }

    public record DeviceInfo(String id, String name, String type, boolean isActive) {}


    public List<DeviceInfo> parseAvailableDevices(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode devicesNode = root.get("devices");

        List<DeviceInfo> devices = new ArrayList<>();

        if (devicesNode != null && devicesNode.isArray()) {
            for (JsonNode deviceNode : devicesNode) {
                String id = deviceNode.get("id").asText();
                String name = deviceNode.get("name").asText();
                String type = deviceNode.get("type").asText();
                boolean isActive = deviceNode.get("is_active").asBoolean();

                devices.add(new DeviceInfo(id, name, type, isActive));
            }
        }

        return devices;
    }


    public String transferToDevice(String accessToken, String deviceId) throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();

        String json = """
                {
                    "device_ids": ["%s"]
                }
                """.formatted(deviceId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/me/player"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }


    public String formatAvailableDevicesResults(List<DeviceInfo> devices) {
        StringBuilder formattedList = new StringBuilder();

        for (DeviceInfo device : devices) {
            formattedList.append(device.toString());
        }
        return formattedList.toString();
    }



    public String getPlaybackState(String accessToken) throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/me/player"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }
}
