package dev.rgbmc.syncable.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.rgbmc.syncable.client.handlers.CommandHandler;
import dev.rgbmc.syncable.client.websocket.SyncableProtocolClient;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class SyncableClient {
  private static final Map<String, CommandHandler> interactiveHandlers = new HashMap<>();
  private static SyncableClient instance;
  private final Logger logger;
  private final UUID serverId;
  private final String host;
  private SyncableProtocolClient protocolClient;
  private boolean close = false;

  public SyncableClient(File workspace, String host) {
    instance = this;
    logger = Logger.getLogger("Syncable-Client");
    this.host = host;
    File serverConfigFile = new File(workspace, "server.yml");
    if (serverConfigFile.exists()) {
      FileConfiguration configuration = YamlConfiguration.loadConfiguration(serverConfigFile);
      serverId = UUID.fromString(configuration.getString("server-id"));
    } else {
      serverId = UUID.randomUUID();
      YamlConfiguration configuration = new YamlConfiguration();
      configuration.set("server-id", serverId.toString());
      try {
        configuration.save(serverConfigFile);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    try {
      protocolClient = new SyncableProtocolClient(host, getServerId());
      protocolClient.connectBlocking();
    } catch (URISyntaxException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static SyncableClient getInstance() {
    return instance;
  }

  public void sendCommand(CommandHandler commandHandler) {
    String command = new Gson().toJson(commandHandler.build());
    getProtocolClient().send(command);
    if (commandHandler.interactive()) {
      interactiveHandlers.put(commandHandler.getMessageId().toString(), commandHandler);
    }
  }

  public void handleCallback(UUID messageId, JsonObject jsonObject) {
    if (interactiveHandlers.containsKey(messageId.toString())) {
      interactiveHandlers.get(messageId.toString()).handle(jsonObject);
      interactiveHandlers.remove(messageId.toString());
    }
  }

  public Logger getLogger() {
    return logger;
  }

  public UUID getServerId() {
    return serverId;
  }

  public SyncableProtocolClient getProtocolClient() {
    return protocolClient;
  }

  public void reconnect() {
    if (!protocolClient.isClosed()) protocolClient.close();
    try {
      protocolClient = new SyncableProtocolClient(host, getServerId());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    this.close = true;
    protocolClient.close();
  }

  public boolean isClose() {
    return close;
  }
}
