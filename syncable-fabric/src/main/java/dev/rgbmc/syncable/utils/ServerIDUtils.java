package dev.rgbmc.syncable.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import net.fabricmc.loader.api.FabricLoader;
import org.fastmcmirror.yaml.file.FileConfiguration;
import org.fastmcmirror.yaml.file.YamlConfiguration;

public class ServerIDUtils {
  public static UUID read() {
    File serverConfigFile =
        new File(
            FabricLoader.getInstance().getConfigDir().toAbsolutePath().toFile(),
            "syncable_server_id.yml");
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
