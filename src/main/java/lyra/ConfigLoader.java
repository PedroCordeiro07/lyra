package lyra;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    public static Properties loadProperties() {
        Properties properties = new Properties();

        try {
            FileInputStream file = new FileInputStream("config/Spotify.properties");
            properties.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;

    }
}
