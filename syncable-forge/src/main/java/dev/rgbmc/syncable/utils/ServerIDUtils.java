package dev.rgbmc.syncable.utils;

import net.minecraftforge.fml.loading.FMLPaths;
import org.fastmcmirror.yaml.file.FileConfiguration;
import org.fastmcmirror.yaml.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ServerIDUtils {
    public static UUID read() {
        File serverConfigFile =
                new File(
                        FMLPaths.CONFIGDIR.get().toAbsolutePath().toFile(),
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
