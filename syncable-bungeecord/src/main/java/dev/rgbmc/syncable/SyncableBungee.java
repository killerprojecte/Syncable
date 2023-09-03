package dev.rgbmc.syncable;

import dev.rgbmc.syncable.server.SyncableServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;

public class SyncableBungee extends Plugin {
    private static SyncableServer syncableServer;

    @Override
    public void onEnable() {
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
        getLogger().info("Platform: BungeeCord");
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
        Configuration configuration;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        syncableServer = new SyncableServer();
        syncableServer.setPort(configuration.getInt("port"));
        Configuration section = configuration.getSection("cockroach");
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

    @Override
    public void onDisable() {
        getLogger().info("Stopping Syncable BungeeCord");
        syncableServer.stop();
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
