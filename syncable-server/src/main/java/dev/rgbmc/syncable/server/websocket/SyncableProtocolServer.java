package dev.rgbmc.syncable.server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rgbmc.syncable.server.SyncableServer;
import dev.rgbmc.syncable.server.handlers.CommandHandlers;
import dev.rgbmc.syncable.server.handlers.CommandHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SyncableProtocolServer extends WebSocketServer {
  public SyncableProtocolServer(InetSocketAddress bindAddress) {
    super(bindAddress);
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    String encoded = conn.getResourceDescriptor().substring(1);
    String serverId = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    SyncableServer.getInstance().getLogger().info("New connection from server: " + serverId);
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    String encoded = conn.getResourceDescriptor().substring(1);
    String serverId = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    SyncableServer.getInstance().getLogger().info("Connection closed: " + serverId);
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
    CommandHandler handler =
        CommandHandlers.getHandler(jsonObject.get("commandId").getAsInt(), jsonObject);
    if (handler.callable()) {
      JsonObject callback = new JsonObject();
      callback.addProperty("messageId", handler.getMessageId().toString());
      callback.addProperty("playerId", handler.getPlayerID().toString());
      try {
        callback.addProperty("data", handler.handle());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      conn.send(new Gson().toJson(callback));
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    SyncableServer.getInstance()
        .getLogger()
        .warning(
            "Error encountered when handling connection: "
                + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    ex.printStackTrace();
  }

  @Override
  public void onStart() {}
}
