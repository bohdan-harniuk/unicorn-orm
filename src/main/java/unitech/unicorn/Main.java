package unitech.unicorn;

import java.net.URL;

// Just for testing purpose. It will be removed soon.
@Deprecated
public class Main {
    public static void main(String[] args) {
        Main application = new Main();
        URL resource = application.getClass().getClassLoader().getResource("db.config");

        if (resource == null) {
            throw new IllegalArgumentException("Configuration File not found!");
        }
        DataSourceConfig.init(resource.getPath());
        // TODO: add limit and offset and like "%%"..
    }
}
