package dev.rgbmc.syncable.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageUtils {
    private final File workspace;
    private FileConfiguration configuration;

    public MessageUtils(File workspace) {
        this.workspace = workspace;
        reload();
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(new File(workspace, "messages.yml"));
    }
}
