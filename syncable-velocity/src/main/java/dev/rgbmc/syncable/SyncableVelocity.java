package dev.rgbmc.syncable;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rgbmc.syncable.server.SyncableServer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.sql.SQLException;


@Plugin(id = "syncable", name = "SyncableVelocity", version = "1.0.0", description = "A Next-Generation Minecraft player data synchronization system based on CockroachDB")
public class SyncableVelocity {

    private static SyncableServer syncableServer;
    private static ConfigurationNode configuration;
    private final ProxyServer server;
    private final Logger logger;
    private final File dataDirectory;

    @Inject
    public SyncableVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory.toAbsolutePath().toFile();
        if (!this.dataDirectory.exists()) this.dataDirectory.mkdirs();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        enable();
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent e) {
        disable();
    }

    private void enable() {
        saveFile("config.yml");
        printLogo(
                "                                                   \n" +
                        "───────────────────────────────────────────────────\n" +
                        "                                                   \n" +
                        "╔═╗┬ ┬┌┐┌┌─┐┌─┐┌┐ ┬  ┌─┐╔═╗┌─┐┬ ┬┌─┐┬─┐┌─┐┌┬┐      \n" +
                        "╚═╗└┬┘││││  ├─┤├┴┐│  ├┤ ╠═╝│ ││││├┤ ├┬┘├┤  ││      \n" +
                        "╚═╝ ┴ ┘└┘└─┘┴ ┴└─┘┴─┘└─┘╩  └─┘└┴┘└─┘┴└─└─┘─┴┘      \n" +
                        "                                                   \n" +
                        "───────────────────────────────────────────────────");
        getLogger().info("Platform: Velocity");
        getLogger().info("Author: FlyProject");
        getLogger().info("Version: " + getServer().getPluginManager().getPlugin("syncable").get().getDescription().getVersion().get());
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
            configuration = YAMLConfigurationLoader.builder().setFile(new File(getDataDirectory(), "config.yml")).build().load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        syncableServer = new SyncableServer();
        syncableServer.setPort(configuration.getNode("port").getInt());
        ConfigurationNode section = configuration.getNode("cockroach");
        try {
            syncableServer.start(
                    section.getNode("host").getString(),
                    section.getNode("database").getString(),
                    section.getNode("use").getString(),
                    section.getNode("password").getString(),
                    section.getNode("ssl").getBoolean(),
                    section.getNode("minimum-idle").getInt(),
                    section.getNode("maximum-pool-size").getInt(),
                    section.getNode("max-lifetime").getInt());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void disable() {
        getLogger().info("Stopping Syncable Velocity");
        syncableServer.stop();
    }

    public Logger getLogger() {
        return logger;
    }

    public File getDataDirectory() {
        return dataDirectory;
    }

    public ProxyServer getServer() {
        return server;
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
                new File(getDataDirectory(), saveName);
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
