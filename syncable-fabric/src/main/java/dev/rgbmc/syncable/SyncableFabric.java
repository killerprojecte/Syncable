package dev.rgbmc.syncable;

import dev.rgbmc.syncable.client.SyncableClient;
import dev.rgbmc.syncable.client.handlers.ReadHandler;
import dev.rgbmc.syncable.client.synchronizers.SynchronizerManager;
import dev.rgbmc.syncable.synchronizers.*;
import dev.rgbmc.syncable.tasks.AutoSaveTimer;
import dev.rgbmc.syncable.utils.ServerIDUtils;
import dev.rgbmc.syncable.utils.SyncUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.Timer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;
import org.fastmcmirror.yaml.configuration.ConfigurationSection;
import org.fastmcmirror.yaml.file.FileConfiguration;
import org.fastmcmirror.yaml.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncableFabric implements ModInitializer {
  private static final Logger logger = LoggerFactory.getLogger("Syncable");
  private static SyncableFabric instance;
  private static FileConfiguration config;
  private static SyncableClient syncableClient;
  private static MinecraftServer server;
  private static Timer timer;

  public static Logger getLogger() {
    return logger;
  }

  public static FileConfiguration getConfig() {
    return config;
  }

  public static SyncableClient getSyncableClient() {
    return syncableClient;
  }

  public static MinecraftServer getServer() {
    return server;
  }

  @Override
  public void onInitialize() {
    ServerLifecycleEvents.SERVER_STARTING.register(
        minecraftServer -> {
          server = minecraftServer;
        });
    ServerLifecycleEvents.SERVER_STOPPING.register(
        minecraftServer -> {
          getLogger().info("Disconnecting Syncable Server");
          getSyncableClient().close();
        });
    ServerLifecycleEvents.SERVER_STARTED.register(
        minecraftServer -> {
          enable();
        });
    ServerPlayConnectionEvents.JOIN.register(
        (handler, sender, server) -> {
          getSyncableClient().sendCommand(new ReadHandler(handler.getPlayer().getUuid()));
        });
    ServerPlayConnectionEvents.DISCONNECT.register(
        ((handler, server) -> {
          SyncUtils.write(handler.getPlayer());
        }));
  }

  private void enable() {
    instance = this;
    saveFile("syncable.yml");
    config =
        YamlConfiguration.loadConfiguration(
            new File(
                FabricLoader.getInstance().getConfigDir().toAbsolutePath().toFile(),
                "syncable.yml"));
    printLogo(
        """
                            \s
                            ────────────────────────────────────────────────
                                                                           \s
                            ╔═╗┬ ┬┌┐┌┌─┐┌─┐┌┐ ┬  ┌─┐╔═╗┌─┐┬ ┬┌─┐┬─┐┌─┐┌┬┐  \s
                            ╚═╗└┬┘││││  ├─┤├┴┐│  ├┤ ╠═╝│ ││││├┤ ├┬┘├┤  ││  \s
                            ╚═╝ ┴ ┘└┘└─┘┴ ┴└─┘┴─┘└─┘╩  └─┘└┴┘└─┘┴└─└─┘─┴┘  \s
                                                                           \s
                            ────────────────────────────────────────────────
                        """);
    getLogger().info("Platform: Fabric");
    getLogger().info("Author: FlyProject");
    Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("syncable");
    modContainer.ifPresent(
        container -> getLogger().info("Version: " + container.getMetadata().getVersion()));
    getLogger().info("");
    getLogger().info("End User License Agreement | 最终用户许可协议 (EULA)");
    getLogger().info("1、 Prohibit any form of distributing build artifacts of this mod");
    getLogger().info("一. 禁止以任何形式传播本模组的构建工件");
    getLogger().info("2. We will not be responsible for any damages caused by using this mod");
    getLogger().info("二、 使用本模组所造成的后果开发团队概不负责");
    getLogger()
        .info(
            "3. We only deal with problems that occur with artifacts obtained from official channels");
    getLogger()
        .info(
            "   If you build and acquire the mod yourself, we will have the right to refuse any support you request from the development team");
    getLogger().info("三、 开发团队仅处理(修复)从官方渠道获取的模组所发生的错误(BUG)");
    getLogger().info("   如果您是通过自行构建等途径获取的工件, 我们将有权拒绝您向开发团队请求的任何支持");
    getLogger().info("");
    getLogger()
        .info(
            "If you are using this mod for the first time, please read the End User License Agreement carefully");
    getLogger().info("如果您是第一次使用本模组, 请仔细阅读最终用户许可协议");
    getLogger().info("");
    getLogger()
        .info(
            "If you do not agree with the content of the agreement, please uninstall the mod, if you continue to use it, you will be deemed to agree to the regulation");
    getLogger().info("如果您不同意协议中的内容请卸载模组, 如继续使用将视为同意调控");
    timer = new Timer("Syncable-Timer");
    ConfigurationSection synchronizers_section =
        getConfig().getConfigurationSection("synchronizers");
    if (synchronizers_section.getBoolean("inventory")) {
      SynchronizerManager.register("inventory", new InventorySynchronizer());
    }
    if (synchronizers_section.getBoolean("experience")) {
      SynchronizerManager.register("experience", new ExperienceSynchronizer());
    }
    if (synchronizers_section.getBoolean("ender-chest")) {
      SynchronizerManager.register("enderChest", new EnderChestSynchronizer());
    }
    if (synchronizers_section.getBoolean("advancement")) {
      SynchronizerManager.register("advancement", new AdvancementSynchronizer());
    }
    if (synchronizers_section.getBoolean("statistic")) {
      SynchronizerManager.register("statistic", new StatisticSynchronizer());
    }
    if (synchronizers_section.getBoolean("effect")) {
      SynchronizerManager.register("effect", new EffectSynchronizer());
    }
    if (synchronizers_section.getBoolean("food")) {
      SynchronizerManager.register("food", new FoodSynchronizer());
    }
    if (synchronizers_section.getBoolean("health")) {
      SynchronizerManager.register("health", new HealthSynchronizer());
    }
    if (synchronizers_section.getBoolean("max-health")) {
      SynchronizerManager.register("max_health", new MaxHealthSynchronizer());
    }
    getServer()
        .execute(
            () -> {
              getLogger().info("Connecting Syncable Server");
              syncableClient =
                  new SyncableClient(
                      ServerIDUtils.read(), getConfig().getString("syncable-server.host"));
              timer.schedule(new AutoSaveTimer(), 0L, 1000L);
            });
  }

  private void saveFile(String name) {
    saveFile(name, false, name);
  }

  private void saveFile(String name, boolean replace, String saveName) {
    URL url = getClass().getClassLoader().getResource(name);
    if (url == null) {
      getLogger().error(name + " Not Found in JarFile");
      return;
    }
    File file =
        new File(FabricLoader.getInstance().getConfigDir().toAbsolutePath().toFile(), saveName);
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
      getLogger().error("Failed unpack file " + name + ":" + e.getMessage());
    }
    connection.setUseCaches(false);
    try {
      saveFile(connection.getInputStream(), file);
    } catch (IOException e) {
      getLogger().error("Failed unpack file " + name + ":" + e.getMessage());
    }
  }

  private void saveFile(InputStream inputStream, File file) throws IOException {
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      int read;
      byte[] bytes = new byte[1024];
      while ((read = inputStream.read(bytes)) != -1) {
        outputStream.write(bytes, 0, read);
      }
    }
  }

  private void printLogo(String text) {
    String[] spilt = text.split("\n");
    for (String l : spilt) {
      getLogger().info(l);
    }
  }
}
