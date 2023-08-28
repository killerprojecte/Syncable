package dev.rgbmc.syncable.client.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.client.SyncableClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class SyncableProtocolClient extends WebSocketClient {
  public SyncableProtocolClient(String host, UUID serverId) throws URISyntaxException {
    super(
        new URI(
            "ws://"
                + host
                + "/"
                + Base64.getEncoder()
                    .encodeToString(serverId.toString().getBytes(StandardCharsets.UTF_8))));
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    SyncableClient.getInstance().getLogger().info("Connected Syncable-Server");
  }

  @Override
  public void onMessage(String message) {
    JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
    UUID messageId = UUID.fromString(jsonObject.get("messageId").getAsString());
    SyncableClient.getInstance().handleCallback(messageId, jsonObject);
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    SyncableClient.getInstance()
        .getLogger()
        .info("Connection closed by code: " + code + " [" + reason + "]");
    if (!SyncableClient.getInstance().isClose()) {
      SyncableClient.getInstance().getLogger().warning("Trying reconnect syncable server...");
      SyncableClient.getInstance().reconnect();
    }
  }

  @Override
  public void onError(Exception ex) {
    SyncableClient.getInstance().getLogger().warning("Error encountered when handling connection");
    ex.printStackTrace();
  }
}
