package dev.rgbmc.syncable;

import dev.rgbmc.syncable.client.SyncableClient;
import dev.rgbmc.syncable.client.schedulers.SyncableScheduler;
import dev.rgbmc.syncable.client.synchronizers.SynchronizerManager;
import dev.rgbmc.syncable.commands.SyncableCommand;
import dev.rgbmc.syncable.listeners.PlayerListener;
import dev.rgbmc.syncable.schedulers.BukkitScheduler;
import dev.rgbmc.syncable.schedulers.FoliaScheduler;
import dev.rgbmc.syncable.server.SyncableServer;
import dev.rgbmc.syncable.synchronizers.*;
import dev.rgbmc.syncable.tasks.AutoSaveTimer;
import dev.rgbmc.syncable.utils.MessageUtils;
import dev.rgbmc.syncable.utils.ServerIDUtils;
import dev.rgbmc.syncable.utils.SyncUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

public class SyncableBukkit extends JavaPlugin {
    public static SyncableBukkit instance;
    private static SyncableServer syncableServer = null;
    private static SyncableClient syncableClient;
    private static SyncableScheduler scheduler;
    private static Timer timer;
    private static MessageUtils messageUtils;

    public static SyncableClient getSyncableClient() {
        return syncableClient;
    }

    public static SyncableServer getSyncableServer() {
        return syncableServer;
    }

    public static SyncableScheduler getScheduler() {
        return scheduler;
    }

    public static MessageUtils getMessageUtils() {
        return messageUtils;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveFile("messages.yml");
        printLogo(
                "                                                   \n" +
                        "───────────────────────────────────────────────────\n" +
                        "                                                   \n" +
                        "╔═╗┬ ┬┌┐┌┌─┐┌─┐┌┐ ┬  ┌─┐╔═╗┌─┐┬ ┬┌─┐┬─┐┌─┐┌┬┐      \n" +
                        "╚═╗└┬┘││││  ├─┤├┴┐│  ├┤ ╠═╝│ ││││├┤ ├┬┘├┤  ││      \n" +
                        "╚═╝ ┴ ┘└┘└─┘┴ ┴└─┘┴─┘└─┘╩  └─┘└┴┘└─┘┴└─└─┘─┴┘      \n" +
                        "                                                   \n" +
                        "───────────────────────────────────────────────────");
        getLogger().info("Platform: Bukkit");
        getLogger().info("Author: FlyProject");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("");
        getLogger().info("End User License Agreement | 最终用户许可协议 (EULA)");
        getLogger().info("1、 Prohibit any form of distributing build artifacts of this plugin");
        getLogger().info("一. 禁止以任何形式传播本插件的构建工件");
        getLogger().info("2. We will not be responsible for any damages caused by using this plugin");
        getLogger().info("二、 使用本插件所造成的后果开发团队概不负责");
        getLogger()
                .info(
                        "3. We only deal with problems that occur with artifacts obtained from official channels");
        getLogger()
                .info(
                        "   If you build and acquire the plugin yourself, we will have the right to refuse any support you request from the development team");
        getLogger().info("三、 开发团队仅处理(修复)从官方渠道获取的插件所发生的错误(BUG)");
        getLogger().info("   如果您是通过自行构建等途径获取的工件, 我们将有权拒绝您向开发团队请求的任何支持");
        getLogger().info("");
        getLogger()
                .info(
                        "If you are using this plugin for the first time, please read the End User License Agreement carefully");
        getLogger().info("如果您是第一次使用本插件, 请仔细阅读最终用户许可协议");
        getLogger().info("");
        getLogger()
                .info(
                        "If you do not agree with the content of the agreement, please uninstall the plugin, if you continue to use it, you will be deemed to agree to the regulation");
        getLogger().info("如果您不同意协议中的内容请卸载插件, 如继续使用将视为同意调控");

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            scheduler = new FoliaScheduler(this);
        } catch (ClassNotFoundException e) {
            scheduler = new BukkitScheduler();
        }

        messageUtils = new MessageUtils(getDataFolder());
        SyncableCommand syncableCommand = new SyncableCommand();
        getCommand("syncable").setExecutor(syncableCommand);
        getCommand("syncable").setTabCompleter(syncableCommand);

        if (getConfig().getBoolean("server-mode")) {
            ConfigurationSection section = getConfig().getConfigurationSection("cockroach");
            syncableServer = new SyncableServer();
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
        }

        timer = new Timer("Syncable-Timer");
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

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
        if (synchronizers_section.getBoolean("persistent-data-container")) {
            SynchronizerManager.register(
                    "persistent_data_container", new PersistentDataContainerSynchronizer());
        }

        CompletableFuture.runAsync(
                () -> {
                    // Waiting when running on server mode
                    if (syncableServer == null) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    getLogger().info("Connecting Syncable Server");
                    syncableClient =
                            new SyncableClient(
                                    ServerIDUtils.read(), getConfig().getString("syncable-server.host"));
                    timer.schedule(new AutoSaveTimer(), 0L, 1000L);
                });
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SyncUtils.write(player);
        }
        getLogger().info("Disconnecting Syncable Client");
        syncableClient.close();
        if (syncableServer != null) {
            getLogger().info("Closing Syncable Server");
            syncableServer.stop();
        }
    }

    private void printLogo(String text) {
        String[] spilt = text.split("\n");
        for (String l : spilt) {
            getLogger().info(l);
        }
    }

    private void saveFile(String name) {
        saveFile(name, false, name);
    }

    private void saveFile(String name, boolean replace, String saveName) {
        URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            getLogger().severe(name + " Not Found in JarFile");
            return;
        }
        File file =
                new File(getDataFolder(), saveName);
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

    private void saveFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}
