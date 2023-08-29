package dev.rgbmc.syncable;

import dev.rgbmc.syncable.server.SyncableServer;
import org.fastmcmirror.yaml.configuration.ConfigurationSection;
import org.fastmcmirror.yaml.file.FileConfiguration;
import org.fastmcmirror.yaml.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SyncableStandalone {
    private final static Logger logger = Logger.getLogger("Syncable-Standalone");
    private static SyncableServer syncableServer;
    private static FileConfiguration configuration;

    public static void main(String[] args) {
        logger.info("Initializing Syncable-Standalone");
        saveFile("config.yml");
        syncableServer = new SyncableServer();
        configuration = YamlConfiguration.loadConfiguration(new File(System.getProperty("user.dir"), "config.yml"));
        syncableServer.setPort(configuration.getInt("port"));
        ConfigurationSection section = configuration.getConfigurationSection("cockroach");
        try {
            syncableServer.start(
                    section.getString("host"),
                    section.getString("database"),
                    section.getString("user"),
                    section.getString("password"),
                    section.getBoolean("ssl"),
                    section.getInt("minimum-idle"),
                    section.getInt("maximum-pool-size"),
                    section.getInt("max-lifetime"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Syncable-Standalone");
            syncableServer.stop();
        }));
    }

    public static Logger getLogger() {
        return logger;
    }

    public static SyncableServer getSyncableServer() {
        return syncableServer;
    }

    public static FileConfiguration getConfiguration() {
        return configuration;
    }

    private static void saveFile(String name) {
        saveFile(name, false, name);
    }

    private static void saveFile(String name, boolean replace, String saveName) {
        URL url = SyncableStandalone.class.getClassLoader().getResource(name);
        if (url == null) {
            getLogger().severe(name + " Not Found in JarFile");
            return;
        }
        File file =
                new File(System.getProperty("user.dir"), saveName);
        if (!replace) {
            if (file.exists()) return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
        connection.setUseCaches(false);
        try {
            saveFile(connection.getInputStream(), file);
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
    }

    private static void saveFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}
