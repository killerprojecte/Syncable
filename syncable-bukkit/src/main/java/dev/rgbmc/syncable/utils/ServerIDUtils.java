package dev.rgbmc.syncable.utils;

import dev.rgbmc.syncable.SyncableBukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ServerIDUtils {
  public static UUID read() {
    File serverConfigFile = new File(SyncableBukkit.instance.getDataFolder(), "server.yml");
    if (serverConfigFile.exists()) {
      FileConfiguration configuration = YamlConfiguration.loadConfiguration(serverConfigFile);
      return UUID.fromString(configuration.getString("server-id"));
    } else {
      UUID serverId = UUID.randomUUID();
      YamlConfiguration configuration = new YamlConfiguration();
      configuration.set("server-id", serverId.toString());
      try {
        configuration.save(serverConfigFile);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return serverId;
    }
  }
}
