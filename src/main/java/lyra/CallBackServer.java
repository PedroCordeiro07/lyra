package lyra;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class CallBackServer {

    private String authorizationCode;

    public void start() throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(3000),
                0
        );

        server.createContext("/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();

            this.authorizationCode = query
                    .replace("code=", "");

            String response = "Lyra authorization received. You can close this window.";

            exchange.sendResponseHeaders(
                    200,
                    response.length()
            );

            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();

            server.stop(1);
        });

        server.start();

        System.out.println("Waiting for Spotify callback...");
    }

    public String getAuthorizationCode() {
        while (authorizationCode == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return authorizationCode;
    }
}
